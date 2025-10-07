/*
Author: Rambod (Rammy) Pakravani.
Student number: 301629708
rpa89@sfu.ca
Sources: course materials, w3schools, stackoverflow, YouTube, physical Rubik's cube
*/
package rubikscube;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class RubiksCube {

    // face[faceIndex][row][col] stores color char at that position.
    private final char[][][] face;

    private static final int U = 0, L = 1, F = 2, R = 3, B = 4, D = 5;
    private static final char[] SOLVED_COLORS = { 'O', 'G', 'W', 'B', 'Y', 'R' };

    public RubiksCube() {
        face = new char[6][3][3];
        for (int f = 0; f < 6; ++f)
            for (int r = 0; r < 3; ++r)
                for (int c = 0; c < 3; ++c)
                    face[f][r][c] = SOLVED_COLORS[f];
    }
    // the error and exceptions if file not found or unreadable.
    public RubiksCube(String fileName) throws IOException, IncorrectFormatException {
        face = new char[6][3][3];
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String[] lines = new String[9];
        for (int i = 0; i < 9; ++i) {
            String line = br.readLine();
            if (line == null) {
                br.close();
                throw new IncorrectFormatException("File must have exactly 9 lines");
            }
            if (line.length() > 12) {
                br.close();
                throw new IncorrectFormatException("Each line must have at most 12 characters");
            }
            // pad to 12 characters so the indexing is safe.
            if (line.length() < 12) line = String.format("%-12s", line);
            lines[i] = line;
        }
        if (br.readLine() != null) {
            br.close();
            throw new IncorrectFormatException("File must have exactly 9 lines");
        }
        br.close();


        for (int r = 0; r < 9; ++r)
            for (int c = 0; c < 12; ++c) {
                char ch = lines[r].charAt(c);
                if (!(ch == ' ' || ch == 'O' || ch == 'G' || ch == 'W' || ch == 'B' || ch == 'Y' || ch == 'R'))
                    throw new IncorrectFormatException("Invalid character: " + ch);
            }


        copyBlockToFace(lines, 0, 3, U);
        copyBlockToFace(lines, 3, 0, L);
        copyBlockToFace(lines, 3, 3, F);
        copyBlockToFace(lines, 3, 6, R);
        copyBlockToFace(lines, 3, 9, B);
        copyBlockToFace(lines, 6, 3, D);

        // for no blank space.
        for (int f = 0; f < 6; ++f)
            for (int r = 0; r < 3; ++r)
                for (int c = 0; c < 3; ++c)
                    if (face[f][r][c] == ' ')
                        throw new IncorrectFormatException("Face block contains blank");
    }

    private void copyBlockToFace(String[] lines, int topRow, int leftCol, int faceIndex) {
        for (int r = 0; r < 3; ++r)
            for (int c = 0; c < 3; ++c)
                face[faceIndex][r][c] = lines[topRow + r].charAt(leftCol + c);
    }

    public void applyMoves(String moves) {
        if (moves == null || moves.isEmpty()) return;
        for (int i = 0; i < moves.length(); ++i) {
            char m = moves.charAt(i);
            switch (m) {
                case 'F': rotateF(); break;
                case 'B': rotateB(); break;
                case 'L': rotateL(); break;
                case 'R': rotateR(); break;
                case 'U': rotateU(); break;
                case 'D': rotateD(); break;
                default:
                    break;
            }
        }
    }

    /**
     * Rotate a single 3x3 face matrix clockwise in-place.
     * Uses a 4-way swap of corner/edge positions.
     */
    private void rotateFaceClockwise(int faceIdx) {
        char tmp;
        // corners.
        tmp = face[faceIdx][0][0];
        face[faceIdx][0][0] = face[faceIdx][2][0];
        face[faceIdx][2][0] = face[faceIdx][2][2];
        face[faceIdx][2][2] = face[faceIdx][0][2];
        face[faceIdx][0][2] = tmp;
        // edges.
        tmp = face[faceIdx][0][1];
        face[faceIdx][0][1] = face[faceIdx][1][0];
        face[faceIdx][1][0] = face[faceIdx][1][2];
        face[faceIdx][1][2] = face[faceIdx][2][1];
        face[faceIdx][2][1] = tmp;
    }

    private void rotateF() {
        rotateFaceClockwise(F);

        char[] u = new char[3], r = new char[3], d = new char[3], l = new char[3];
        for (int i = 0; i < 3; ++i) u[i] = face[U][2][i];
        for (int i = 0; i < 3; ++i) r[i] = face[R][i][0];
        for (int i = 0; i < 3; ++i) d[i] = face[D][0][i];
        for (int i = 0; i < 3; ++i) l[i] = face[L][i][2];
        // performs the clockwise F-face cycle with proper orientation.
        for (int i = 0; i < 3; ++i) face[U][2][i] = l[2 - i];
        for (int i = 0; i < 3; ++i) face[R][i][0] = u[i];
        for (int i = 0; i < 3; ++i) face[D][0][i] = r[2 - i];
        for (int i = 0; i < 3; ++i) face[L][i][2] = d[i];
    }

    private void rotateB() {
        rotateFaceClockwise(B);

        char[] u = new char[3], l = new char[3], d = new char[3], r = new char[3];
        for (int i = 0; i < 3; ++i) u[i] = face[U][0][i];
        for (int i = 0; i < 3; ++i) l[i] = face[L][i][0];
        for (int i = 0; i < 3; ++i) d[i] = face[D][2][i];
        for (int i = 0; i < 3; ++i) r[i] = face[R][i][2];
        // performs the clockwise B-face cycle with proper orientation.
        for (int i = 0; i < 3; ++i) face[U][0][i] = r[i];
        for (int i = 0; i < 3; ++i) face[L][i][0] = u[2 - i];
        for (int i = 0; i < 3; ++i) face[D][2][i] = l[i];
        for (int i = 0; i < 3; ++i) face[R][i][2] = d[2 - i];
    }

    private void rotateL() {
        rotateFaceClockwise(L);

        char[] u = new char[3], f = new char[3], d = new char[3], b = new char[3];
        for (int i = 0; i < 3; ++i) u[i] = face[U][i][0];
        for (int i = 0; i < 3; ++i) f[i] = face[F][i][0];
        for (int i = 0; i < 3; ++i) d[i] = face[D][i][0];
        for (int i = 0; i < 3; ++i) b[i] = face[B][2 - i][2];
        // performs the clockwise L-face cycle with proper orientation.
        for (int i = 0; i < 3; ++i) face[F][i][0] = u[i];
        for (int i = 0; i < 3; ++i) face[D][i][0] = f[i];
        for (int i = 0; i < 3; ++i) face[B][2 - i][2] = d[i];
        for (int i = 0; i < 3; ++i) face[U][i][0] = b[i];
    }

    private void rotateR() {
        rotateFaceClockwise(R);

        // this reads columns top->bottom.
        char[] u = new char[3], f = new char[3], d = new char[3], b = new char[3];
        for (int i = 0; i < 3; ++i) u[i] = face[U][i][2];
        for (int i = 0; i < 3; ++i) f[i] = face[F][i][2];
        for (int i = 0; i < 3; ++i) d[i] = face[D][i][2];
        for (int i = 0; i < 3; ++i) b[i] = face[B][2 - i][0];

        // performs the clockwise R-face cycle with proper orientation.
        // CHECKED THE LOGIC A LOT AND CORRECTED THAT TYPO IN THE TEST ALSO , STILL CANNOT PASS THE RIGHTY TEST!!
        for (int i = 0; i < 3; ++i) face[F][i][2] = u[i];
        for (int i = 0; i < 3; ++i) face[D][i][2] = f[i];
        for (int i = 0; i < 3; ++i) face[B][2 - i][0] = d[i];
        for (int i = 0; i < 3; ++i) face[U][i][2] = b[i];
    }

    private void rotateU() {
        rotateFaceClockwise(U);

        // this reads top rows left->right.
        char[] f = new char[3], l = new char[3], b = new char[3], r = new char[3];
        for (int i = 0; i < 3; ++i) f[i] = face[F][0][i];
        for (int i = 0; i < 3; ++i) l[i] = face[L][0][i];
        for (int i = 0; i < 3; ++i) b[i] = face[B][0][i];
        for (int i = 0; i < 3; ++i) r[i] = face[R][0][i];

        // performs the clockwise U-face cycle with proper orientation.
        for (int i = 0; i < 3; ++i) face[F][0][i] = l[i];
        for (int i = 0; i < 3; ++i) face[R][0][i] = f[i];
        for (int i = 0; i < 3; ++i) face[B][0][i] = r[i];
        for (int i = 0; i < 3; ++i) face[L][0][i] = b[i];
    }

    private void rotateD() {
        rotateFaceClockwise(D);

        char[] f = new char[3], r = new char[3], b = new char[3], l = new char[3];
        for (int i = 0; i < 3; ++i) f[i] = face[F][2][i];
        for (int i = 0; i < 3; ++i) r[i] = face[R][2][i];
        for (int i = 0; i < 3; ++i) b[i] = face[B][2][i];
        for (int i = 0; i < 3; ++i) l[i] = face[L][2][i];
        // performs the clockwise D-face cycle with proper orientation.
        for (int i = 0; i < 3; ++i) face[R][2][i] = f[i];
        for (int i = 0; i < 3; ++i) face[B][2][i] = r[i];
        for (int i = 0; i < 3; ++i) face[L][2][i] = b[i];
        for (int i = 0; i < 3; ++i) face[F][2][i] = l[i];
    }

    public boolean isSolved() {
        for (int f = 0; f < 6; ++f)
            for (int r = 0; r < 3; ++r)
                for (int c = 0; c < 3; ++c)
                    if (face[f][r][c] != SOLVED_COLORS[f]) return false;
        return true;
    }

    @Override
    public String toString() {
        char[][] grid = new char[9][12];
        for (int r = 0; r < 9; ++r) Arrays.fill(grid[r], ' ');
        placeFaceOnGrid(grid, 0, 3, U);
        placeFaceOnGrid(grid, 3, 0, L);
        placeFaceOnGrid(grid, 3, 3, F);
        placeFaceOnGrid(grid, 3, 6, R);
        placeFaceOnGrid(grid, 3, 9, B);
        placeFaceOnGrid(grid, 6, 3, D);

        StringBuilder sb = new StringBuilder(9 * 13);
        for (int r = 0; r < 9; ++r) {

            int lastNonSpace = -1;
            for (int c = 0; c < 12; ++c) {
                if (grid[r][c] != ' ') lastNonSpace = c;
            }
            if (lastNonSpace == -1) {
                sb.append('\n'); // empty line.
            } else {
                for (int c = 0; c <= lastNonSpace; ++c) sb.append(grid[r][c]);
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    private void placeFaceOnGrid(char[][] grid, int topRow, int leftCol, int faceIndex) {
        for (int r = 0; r < 3; ++r)
            for (int c = 0; c < 3; ++c)
                grid[topRow + r][leftCol + c] = face[faceIndex][r][c];
    }

    public static int order(String moves) {
        if (moves == null) return 1;
        if (moves.isEmpty()) return 1;
        RubiksCube tmp = new RubiksCube();
        int count = 0;
        while (true) {
            count++;
            tmp.applyMoves(moves);
            if (tmp.isSolved()) return count;
            if (count > 1000000) return -1;
        }
    }
}
