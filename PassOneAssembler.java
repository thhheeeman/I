import java.util.*;

public class PassOneAssembler 
{

    static String[] mnemonics = 
    {
        "START", "END", "READ", "PRINT", "MOVER", "MOVEM", "ADD", "SUB", "MULT", "DS", "DC", "STOP"
    };
    static String[] types = 
    {
        "AD", "AD", "IS", "IS", "IS", "IS", "IS", "IS", "IS", "DL", "DL", "IS"
    };
    static int[] opcodes = 
    {
        1, 2, 9, 10, 4, 5, 1, 2, 3, 1, 2, 0
    };

    static String[] symbolTable = new String[50];
    static int[] symbolAddr = new int[50];
    static int symCount = 0;

    static String[] literalTable = new String[50];
    static int[] literalAddr = new int[50];
    static int litCount = 0;

    static List<String[]> intermediateCode = new ArrayList<>();
    static int LC = 0;
    static int nextLitAddr = 200;

    public static void main(String[] args) 
    {
        Scanner sc = new Scanner(System.in);
        List<String> program = new ArrayList<>();

        System.out.println("Enter assembly program line by line (type END to finish):");

        while (true) 
        {
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) 
            {
                program.add(line);
                if (line.equalsIgnoreCase("END")) break;
            }
        }

        passOne(program);
        printTables();

        sc.close();
    }

    static void passOne(List<String> program) 
    {
        System.out.println("\n--- PASS ONE ---");

        for (String line : program) 
        {
            if (line.isEmpty()) continue;

            String[] parts = line.trim().split("\\s+|,");
            int partIndex = 0;

            String label = null;
            String mnemonic = null;
            String op1 = null;
            String op2 = null;

            // Check for label
            if (parts.length > 0 && !isMnemonic(parts[partIndex])) 
            {
                label = parts[partIndex++];
                addSymbol(label, LC);
            }

            // Check if mnemonic exists
            if (partIndex < parts.length) 
            {
                mnemonic = parts[partIndex++];
            } else {
                System.out.println("⚠️ Warning: Missing mnemonic in line: " + Arrays.toString(parts));
                continue;
            }

            if (partIndex < parts.length) op1 = parts[partIndex];
            if (partIndex + 1 < parts.length) op2 = parts[partIndex + 1];

            int mIndex = getMnemonicIndex(mnemonic);
            if (mIndex == -1) {
                System.out.println("⚠️ Unknown mnemonic: " + mnemonic + " at LC=" + LC);
                continue;
            }

            String type = types[mIndex];
            int opcode = opcodes[mIndex];
            String[] icLine = new String[4];
            icLine[0] = type;
            icLine[1] = String.format("%02d", opcode);

            if (mnemonic.equals("START")) {
                LC = Integer.parseInt(op1);
                icLine[2] = "C";
                icLine[3] = op1;
            } else if (mnemonic.equals("END")) {
                icLine[2] = "-";
                icLine[3] = "-";
            } else if (mnemonic.equals("DS") || mnemonic.equals("DC")) {
                addSymbol(label, LC);
                icLine[2] = "C";
                icLine[3] = op1;
                LC += 1;
            } else {
                if (op1 != null && op1.startsWith("=")) {
                    addLiteral(op1);
                } else if (op1 != null && isRegister(op1)) {
                    icLine[2] = String.valueOf(getRegisterCode(op1));
                } else if (op1 != null) {
                    addSymbol(op1, -1);
                }

                if (op2 != null && op2.startsWith("=")) {
                    addLiteral(op2);
                    icLine[3] = op2;
                } else if (op2 != null) {
                    addSymbol(op2, -1);
                    icLine[3] = op2;
                } else if (op1 != null && !isRegister(op1)) {
                    icLine[3] = op1;
                }

                LC++;
            }

            intermediateCode.add(icLine);
        }
    }

    static void printTables() {
        // Symbol Table
        System.out.println("\n---------------------");
        System.out.println("| Symbol | Address  |");
        System.out.println("---------------------");
        for (int i = 0; i < symCount; i++) {
            System.out.printf("| %-6s | %-8d |\n", symbolTable[i], symbolAddr[i]);
        }
        System.out.println("---------------------");

        // Literal Table
        System.out.println("\n-----------------------");
        System.out.println("| Literal | Address   |");
        System.out.println("-----------------------");
        for (int i = 0; i < litCount; i++) {
            System.out.printf("| %-7s | %-9d |\n", literalTable[i], literalAddr[i]);
        }
        System.out.println("-----------------------");

        // Intermediate Code
        System.out.println("\n--------------------------------------------------");
        System.out.println("| Line | Type | Opcode | Op1 | Op2               |");
        System.out.println("--------------------------------------------------");
        for (int i = 0; i < intermediateCode.size(); i++) {
            String[] line = intermediateCode.get(i);
            String op1 = (line.length > 2 && line[2] != null) ? line[2] : "-";
            String op2 = (line.length > 3 && line[3] != null) ? line[3] : "-";
            System.out.printf("| %-4d | %-4s | %-6s | %-3s | %-17s |\n", i, line[0], line[1], op1, op2);
        }
        System.out.println("--------------------------------------------------");
    }

    // Symbol functions
    static void addSymbol(String symbol, int addr) {
        for (int i = 0; i < symCount; i++) {
            if (symbolTable[i].equals(symbol)) return;
        }
        symbolTable[symCount] = symbol;
        symbolAddr[symCount] = addr;
        symCount++;
    }

    // Literal functions
    static void addLiteral(String literal) {
        for (int i = 0; i < litCount; i++) {
            if (literalTable[i].equals(literal)) return;
        }
        literalTable[litCount] = literal;
        literalAddr[litCount] = nextLitAddr++;
        litCount++;
    }

    static boolean isMnemonic(String s) {
        return Arrays.asList(mnemonics).contains(s);
    }

    static int getMnemonicIndex(String mnem) {
        for (int i = 0; i < mnemonics.length; i++) {
            if (mnemonics[i].equals(mnem)) return i;
        }
        return -1;
    }

    static boolean isRegister(String s) {
        return s.equals("AREG") || s.equals("BREG") || s.equals("CREG");
    }

    static int getRegisterCode(String reg) {
        switch (reg) {
            case "AREG": return 1;
            case "BREG": return 2;
            case "CREG": return 3;
            default: return 0;
        }
    }
}
