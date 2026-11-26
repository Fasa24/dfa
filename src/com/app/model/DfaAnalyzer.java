package com.app.model;

import java.util.ArrayList;
import java.util.List;

public class DfaAnalyzer {
    // Categorias de simbolo (columnas de la matriz)
    // 0: DIG (0-9), 1: MON ($), 2: COM (,), 3: PUN (.), 4: POR (%), 5: SIG (-), 6: OTR
    private static final int COL_DIG = 0;
    private static final int COL_MON = 1;
    private static final int COL_COM = 2;
    private static final int COL_PUN = 3;
    private static final int COL_POR = 4;
    private static final int COL_SIG = 5;
    private static final int COL_OTR = 6;

    // Estados del AFD (filas de la matriz)
    private static final int STATE_Q0 = 0;              // Inicio
    private static final int STATE_Q1 = 1;              // $
    private static final int STATE_Q2 = 2;              // Aceptacion entero (1er digito antes de ,)
    private static final int STATE_Q3 = 3;              // , (1er digito miles)
    private static final int STATE_Q4 = 4;              // 2do digito miles
    private static final int STATE_Q5 = 5;              // 3er digito miles
    private static final int STATE_Q6 = 6;              // Aceptacion grupo de miles
    private static final int STATE_Q7 = 7;              // . (decimal)
    private static final int STATE_Q8 = 8;              // Aceptacion decimal (Real / Monetario)
    private static final int STATE_Q9 = 9;              // Aceptacion porcentaje
    private static final int STATE_QE = 10;             // Estado muerto / error
    private static final int STATE_Q_NEG = 11;          // - inicial (espera digito o $)
    private static final int STATE_Q_NEG_DIG = 12;      // Aceptacion entero negativo (Real)
    private static final int STATE_Q_NEG_PUN = 13;      // -digito(s). (espera decimal)
    private static final int STATE_Q_NEG_DEC = 14;      // Aceptacion decimal negativo (Real)
    private static final int STATE_Q_NEG_MON = 15;      // -$ (espera digito monetario)
    private static final int STATE_Q_NEG_MON_DIG = 16;  // Aceptacion -$digito(s) (Valor monetario)
    private static final int STATE_Q_NEG_MON_COM = 17;  // -$... , (1er digito miles)
    private static final int STATE_Q_NEG_MON_C1 = 18;   // 2do digito miles
    private static final int STATE_Q_NEG_MON_C2 = 19;   // 3er digito miles
    private static final int STATE_Q_NEG_MON_C3 = 20;   // Aceptacion -$... miles (Valor monetario)
    private static final int STATE_Q_NEG_MON_PUN = 21;  // -$... . (espera decimal)
    private static final int STATE_Q_NEG_MON_DEC = 22;  // Aceptacion -$... decimal (Valor monetario)
    private static final int STATE_Q2_D2 = 23;          // 2do digito inicial (acepta , o 3er digito)
    private static final int STATE_Q2_D3 = 24;          // 3er digito inicial (acepta , o 4to digito sin coma)
    private static final int STATE_Q2_INT = 25;         // 4to+ digito sin coma (rechaza , para evitar 5000,000)

    // Matriz de transiciones [estadoActual][categoriaCaracter] -> estadoSiguiente
    private static final int[][] TRANSITION_TABLE = {
            // DIG                MON                  COM                  PUN                   POR         SIG           OTR
            { STATE_Q2,           STATE_Q1,            STATE_QE,            STATE_QE,             STATE_QE,   STATE_Q_NEG,  STATE_QE }, // q0
            { STATE_Q2,           STATE_QE,            STATE_QE,            STATE_QE,             STATE_QE,   STATE_QE,     STATE_QE }, // q1
            { STATE_Q2_D2,        STATE_QE,            STATE_Q3,            STATE_Q7,             STATE_Q9,   STATE_QE,     STATE_QE }, // q2
            { STATE_Q4,           STATE_QE,            STATE_QE,            STATE_QE,             STATE_QE,   STATE_QE,     STATE_QE }, // q3
            { STATE_Q5,           STATE_QE,            STATE_QE,            STATE_QE,             STATE_QE,   STATE_QE,     STATE_QE }, // q4
            { STATE_Q6,           STATE_QE,            STATE_QE,            STATE_QE,             STATE_QE,   STATE_QE,     STATE_QE }, // q5
            { STATE_QE,           STATE_QE,            STATE_Q3,            STATE_Q7,             STATE_Q9,   STATE_QE,     STATE_QE }, // q6
            { STATE_Q8,           STATE_QE,            STATE_QE,            STATE_QE,             STATE_QE,   STATE_QE,     STATE_QE }, // q7
            { STATE_Q8,           STATE_QE,            STATE_QE,            STATE_QE,             STATE_Q9,   STATE_QE,     STATE_QE }, // q8
            { STATE_QE,           STATE_QE,            STATE_QE,            STATE_QE,             STATE_QE,   STATE_QE,     STATE_QE }, // q9
            { STATE_QE,           STATE_QE,            STATE_QE,            STATE_QE,             STATE_QE,   STATE_QE,     STATE_QE }, // qE
            { STATE_Q_NEG_DIG,    STATE_Q_NEG_MON,     STATE_QE,            STATE_QE,             STATE_QE,   STATE_QE,     STATE_QE }, // qNeg
            { STATE_Q_NEG_DIG,    STATE_QE,            STATE_QE,            STATE_Q_NEG_PUN,      STATE_Q9,   STATE_QE,     STATE_QE }, // qNegDig
            { STATE_Q_NEG_DEC,    STATE_QE,            STATE_QE,            STATE_QE,             STATE_QE,   STATE_QE,     STATE_QE }, // qNegPun
            { STATE_Q_NEG_DEC,    STATE_QE,            STATE_QE,            STATE_QE,             STATE_Q9,   STATE_QE,     STATE_QE }, // qNegDec
            { STATE_Q_NEG_MON_DIG,STATE_QE,            STATE_QE,            STATE_QE,             STATE_QE,   STATE_QE,     STATE_QE }, // qNegMon
            { STATE_Q_NEG_MON_DIG,STATE_QE,            STATE_Q_NEG_MON_COM, STATE_Q_NEG_MON_PUN,  STATE_QE,   STATE_QE,     STATE_QE }, // qNegMonDig
            { STATE_Q_NEG_MON_C1, STATE_QE,            STATE_QE,            STATE_QE,             STATE_QE,   STATE_QE,     STATE_QE }, // qNegMonCom
            { STATE_Q_NEG_MON_C2, STATE_QE,            STATE_QE,            STATE_QE,             STATE_QE,   STATE_QE,     STATE_QE }, // qNegMonC1
            { STATE_Q_NEG_MON_C3, STATE_QE,            STATE_QE,            STATE_QE,             STATE_QE,   STATE_QE,     STATE_QE }, // qNegMonC2
            { STATE_QE,           STATE_QE,            STATE_Q_NEG_MON_COM, STATE_Q_NEG_MON_PUN,  STATE_QE,   STATE_QE,     STATE_QE }, // qNegMonC3
            { STATE_Q_NEG_MON_DEC,STATE_QE,            STATE_QE,            STATE_QE,             STATE_QE,   STATE_QE,     STATE_QE }, // qNegMonPun
            { STATE_Q_NEG_MON_DEC,STATE_QE,            STATE_QE,            STATE_QE,             STATE_QE,   STATE_QE,     STATE_QE }, // qNegMonDec
            { STATE_Q2_D3,        STATE_QE,            STATE_Q3,            STATE_Q7,             STATE_Q9,   STATE_QE,     STATE_QE }, // q2_d2
            { STATE_Q2_INT,       STATE_QE,            STATE_Q3,            STATE_Q7,             STATE_Q9,   STATE_QE,     STATE_QE }, // q2_d3
            { STATE_Q2_INT,       STATE_QE,            STATE_QE,            STATE_Q7,             STATE_Q9,   STATE_QE,     STATE_QE }  // q2_int
    };

    public List<NumericToken> analyzeText(String text) {
        List<NumericToken> results = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            return results;
        }

        List<String> lines = splitByNewLine(text);
        int resultCounter = 1;

        for (int i = 0; i < lines.size(); i++) {
            int lineNumber = i + 1;
            List<String> candidateTokens = extractCandidateTokens(lines.get(i));

            for (String rawToken : candidateTokens) {
                String cleanedToken = trimTrailingPunctuation(rawToken);
                String type = evaluateTokenWithDfa(cleanedToken);

                if (!"Invalid".equals(type)) {
                    results.add(new NumericToken(resultCounter, lineNumber, cleanedToken, type));
                    resultCounter++;
                }
            }
        }

        return results;
    }

    private List<String> splitByNewLine(String text) {
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                lines.add(current.toString());
                current.setLength(0);
            }
            if (c != '\n' && c != '\r') {
                current.append(c);
            }
        }

        lines.add(current.toString());
        return lines;
    }

    private List<String> extractCandidateTokens(String lineText) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < lineText.length(); i++) {
            char c = lineText.charAt(i);
            if (!Character.isWhitespace(c)) {
                current.append(c);
            }
            if (Character.isWhitespace(c) && !current.isEmpty()) {
                tokens.add(current.toString());
                current.setLength(0);
            }
        }

        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }

        return tokens;
    }

    private String trimTrailingPunctuation(String token) {
        int end = token.length();
        while (end > 0 && shouldTrimEndChar(token.charAt(end - 1))) {
            end--;
        }
        return token.substring(0, end);
    }

    private boolean shouldTrimEndChar(char last) {
        return last == '.' || last == ',' || last == ';' || last == ':';
    }

    private int mapCharToColumn(char c) {
        if (c >= '0' && c <= '9') {
            return COL_DIG;
        }
        if (c == '$') {
            return COL_MON;
        }
        if (c == ',') {
            return COL_COM;
        }
        if (c == '.') {
            return COL_PUN;
        }
        if (c == '%') {
            return COL_POR;
        }
        if (c == '-') {
            return COL_SIG;
        }
        return COL_OTR;
    }

    private String evaluateTokenWithDfa(String token) {
        if (token.isEmpty()) {
            return "Invalid";
        }

        int currentState = STATE_Q0;
        boolean hasDollarPrefix = (token.charAt(0) == '$');

        for (int i = 0; i < token.length(); i++) {
            int col = mapCharToColumn(token.charAt(i));
            currentState = TRANSITION_TABLE[currentState][col];
        }

        return classifyFinalState(currentState, hasDollarPrefix);
    }

    private String classifyFinalState(int finalState, boolean hasDollarPrefix) {
        if (finalState == STATE_Q9) {
            return "Percentage";
        }
        if (isMonetaryAcceptedState(finalState, hasDollarPrefix)) {
            return "Monetary Value";
        }
        if (isRealAcceptedState(finalState)) {
            return "Real";
        }
        if (isNaturalAcceptedState(finalState)) {
            return "Natural";
        }
        return "Invalid";
    }

    private boolean isMonetaryAcceptedState(int state, boolean hasDollarPrefix) {
        boolean isPositiveDollarState = hasDollarPrefix
                && (state == STATE_Q2 || state == STATE_Q2_D2 || state == STATE_Q2_D3 || state == STATE_Q2_INT || state == STATE_Q6 || state == STATE_Q8);
        return isPositiveDollarState || isNegativeMonetaryAcceptedState(state);
    }

    private boolean isNegativeMonetaryAcceptedState(int state) {
        return state == STATE_Q_NEG_MON_DIG
                || state == STATE_Q_NEG_MON_C3
                || state == STATE_Q_NEG_MON_DEC;
    }

    private boolean isRealAcceptedState(int state) {
        return state == STATE_Q8 || state == STATE_Q_NEG_DIG || state == STATE_Q_NEG_DEC;
    }

    private boolean isNaturalAcceptedState(int state) {
        return state == STATE_Q2 || state == STATE_Q2_D2 || state == STATE_Q2_D3 || state == STATE_Q2_INT || state == STATE_Q6;
    }
}
