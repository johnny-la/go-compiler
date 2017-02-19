/* This file was generated by SableCC (http://www.sablecc.org/). */

package golite.lexer;

import java.io.*;
import golite.node.*;

@SuppressWarnings("nls")
public class Lexer
{
    protected Token token;
    protected State state = State.INITIAL;

    private IPushbackReader in;
    private int line;
    private int pos;
    private boolean cr;
    private boolean eof;
    private final StringBuffer text = new StringBuffer();

    @SuppressWarnings("unused")
    protected void filter() throws LexerException, IOException
    {
        // Do nothing
    }

    public Lexer(@SuppressWarnings("hiding") final PushbackReader in)
    {
        this.in = new IPushbackReader() {

            private PushbackReader pushbackReader = in;
            
            @Override
            public void unread(int c) throws IOException {
                pushbackReader.unread(c);
            }
            
            @Override
            public int read() throws IOException {
                return pushbackReader.read();
            }
        };
    }
 
    public Lexer(@SuppressWarnings("hiding") IPushbackReader in)
    {
        this.in = in;
    }
 
    public Token peek() throws LexerException, IOException
    {
        while(this.token == null)
        {
            this.token = getToken();
            filter();
        }

        return this.token;
    }

    public Token next() throws LexerException, IOException
    {
        while(this.token == null)
        {
            this.token = getToken();
            filter();
        }

        Token result = this.token;
        this.token = null;
        return result;
    }

    protected Token getToken() throws IOException, LexerException
    {
        int dfa_state = 0;

        int start_pos = this.pos;
        int start_line = this.line;

        int accept_state = -1;
        int accept_token = -1;
        int accept_length = -1;
        int accept_pos = -1;
        int accept_line = -1;

        @SuppressWarnings("hiding") int[][][] gotoTable = Lexer.gotoTable[this.state.id()];
        @SuppressWarnings("hiding") int[] accept = Lexer.accept[this.state.id()];
        this.text.setLength(0);

        while(true)
        {
            int c = getChar();

            if(c != -1)
            {
                switch(c)
                {
                case 10:
                    if(this.cr)
                    {
                        this.cr = false;
                    }
                    else
                    {
                        this.line++;
                        this.pos = 0;
                    }
                    break;
                case 13:
                    this.line++;
                    this.pos = 0;
                    this.cr = true;
                    break;
                default:
                    this.pos++;
                    this.cr = false;
                    break;
                }

                this.text.append((char) c);

                do
                {
                    int oldState = (dfa_state < -1) ? (-2 -dfa_state) : dfa_state;

                    dfa_state = -1;

                    int[][] tmp1 =  gotoTable[oldState];
                    int low = 0;
                    int high = tmp1.length - 1;

                    while(low <= high)
                    {
                        // int middle = (low + high) / 2;
                        int middle = (low + high) >>> 1;
                        int[] tmp2 = tmp1[middle];

                        if(c < tmp2[0])
                        {
                            high = middle - 1;
                        }
                        else if(c > tmp2[1])
                        {
                            low = middle + 1;
                        }
                        else
                        {
                            dfa_state = tmp2[2];
                            break;
                        }
                    }
                }while(dfa_state < -1);
            }
            else
            {
                dfa_state = -1;
            }

            if(dfa_state >= 0)
            {
                if(accept[dfa_state] != -1)
                {
                    accept_state = dfa_state;
                    accept_token = accept[dfa_state];
                    accept_length = this.text.length();
                    accept_pos = this.pos;
                    accept_line = this.line;
                }
            }
            else
            {
                if(accept_state != -1)
                {
                    switch(accept_token)
                    {
                    case 0:
                        {
                            @SuppressWarnings("hiding") Token token = new0(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 1:
                        {
                            @SuppressWarnings("hiding") Token token = new1(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 2:
                        {
                            @SuppressWarnings("hiding") Token token = new2(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 3:
                        {
                            @SuppressWarnings("hiding") Token token = new3(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 4:
                        {
                            @SuppressWarnings("hiding") Token token = new4(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 5:
                        {
                            @SuppressWarnings("hiding") Token token = new5(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 6:
                        {
                            @SuppressWarnings("hiding") Token token = new6(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 7:
                        {
                            @SuppressWarnings("hiding") Token token = new7(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 8:
                        {
                            @SuppressWarnings("hiding") Token token = new8(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 9:
                        {
                            @SuppressWarnings("hiding") Token token = new9(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 10:
                        {
                            @SuppressWarnings("hiding") Token token = new10(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 11:
                        {
                            @SuppressWarnings("hiding") Token token = new11(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 12:
                        {
                            @SuppressWarnings("hiding") Token token = new12(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 13:
                        {
                            @SuppressWarnings("hiding") Token token = new13(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 14:
                        {
                            @SuppressWarnings("hiding") Token token = new14(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 15:
                        {
                            @SuppressWarnings("hiding") Token token = new15(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 16:
                        {
                            @SuppressWarnings("hiding") Token token = new16(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 17:
                        {
                            @SuppressWarnings("hiding") Token token = new17(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 18:
                        {
                            @SuppressWarnings("hiding") Token token = new18(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 19:
                        {
                            @SuppressWarnings("hiding") Token token = new19(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 20:
                        {
                            @SuppressWarnings("hiding") Token token = new20(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 21:
                        {
                            @SuppressWarnings("hiding") Token token = new21(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 22:
                        {
                            @SuppressWarnings("hiding") Token token = new22(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 23:
                        {
                            @SuppressWarnings("hiding") Token token = new23(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 24:
                        {
                            @SuppressWarnings("hiding") Token token = new24(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    case 25:
                        {
                            @SuppressWarnings("hiding") Token token = new25(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            this.pos = accept_pos;
                            this.line = accept_line;
                            return token;
                        }
                    }
                }
                else
                {
                    if(this.text.length() > 0)
                    {
                        throw new LexerException(
                            new InvalidToken(this.text.substring(0, 1), start_line + 1, start_pos + 1),
                            "[" + (start_line + 1) + "," + (start_pos + 1) + "]" +
                            " Unknown token: " + this.text);
                    }

                    @SuppressWarnings("hiding") EOF token = new EOF(
                        start_line + 1,
                        start_pos + 1);
                    return token;
                }
            }
        }
    }

    Token new0(@SuppressWarnings("hiding") String text, @SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TWhitespace(text, line, pos); }
    Token new1(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TPlus(line, pos); }
    Token new2(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TMinus(line, pos); }
    Token new3(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TStar(line, pos); }
    Token new4(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TSlash(line, pos); }
    Token new5(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TSemicolon(line, pos); }
    Token new6(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TLParen(line, pos); }
    Token new7(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TRParen(line, pos); }
    Token new8(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TEquals(line, pos); }
    Token new9(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TColon(line, pos); }
    Token new10(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TVar(line, pos); }
    Token new11(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TRead(line, pos); }
    Token new12(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TPrint(line, pos); }
    Token new13(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TWhile(line, pos); }
    Token new14(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TDo(line, pos); }
    Token new15(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TDone(line, pos); }
    Token new16(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TIf(line, pos); }
    Token new17(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TThen(line, pos); }
    Token new18(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TElse(line, pos); }
    Token new19(@SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TEndif(line, pos); }
    Token new20(@SuppressWarnings("hiding") String text, @SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TComment(text, line, pos); }
    Token new21(@SuppressWarnings("hiding") String text, @SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TType(text, line, pos); }
    Token new22(@SuppressWarnings("hiding") String text, @SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TId(text, line, pos); }
    Token new23(@SuppressWarnings("hiding") String text, @SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TInt(text, line, pos); }
    Token new24(@SuppressWarnings("hiding") String text, @SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TFloat(text, line, pos); }
    Token new25(@SuppressWarnings("hiding") String text, @SuppressWarnings("hiding") int line, @SuppressWarnings("hiding") int pos) { return new TString(text, line, pos); }

    private int getChar() throws IOException
    {
        if(this.eof)
        {
            return -1;
        }

        int result = this.in.read();

        if(result == -1)
        {
            this.eof = true;
        }

        return result;
    }

    private void pushBack(int acceptLength) throws IOException
    {
        int length = this.text.length();
        for(int i = length - 1; i >= acceptLength; i--)
        {
            this.eof = false;

            this.in.unread(this.text.charAt(i));
        }
    }

    protected void unread(@SuppressWarnings("hiding") Token token) throws IOException
    {
        @SuppressWarnings("hiding") String text = token.getText();
        int length = text.length();

        for(int i = length - 1; i >= 0; i--)
        {
            this.eof = false;

            this.in.unread(text.charAt(i));
        }

        this.pos = token.getPos() - 1;
        this.line = token.getLine() - 1;
    }

    private String getText(int acceptLength)
    {
        StringBuffer s = new StringBuffer(acceptLength);
        for(int i = 0; i < acceptLength; i++)
        {
            s.append(this.text.charAt(i));
        }

        return s.toString();
    }

    private static int[][][][] gotoTable;
/*  {
        { // INITIAL
            {{9, 9, 1}, {10, 10, 2}, {13, 13, 3}, {32, 32, 4}, {34, 34, 5}, {40, 40, 6}, {41, 41, 7}, {42, 42, 8}, {43, 43, 9}, {45, 45, 10}, {47, 47, 11}, {48, 48, 12}, {49, 57, 13}, {58, 58, 14}, {59, 59, 15}, {61, 61, 16}, {65, 90, 17}, {95, 95, 18}, {97, 99, 19}, {100, 100, 20}, {101, 101, 21}, {102, 102, 22}, {103, 104, 19}, {105, 105, 23}, {106, 111, 19}, {112, 112, 24}, {113, 113, 19}, {114, 114, 25}, {115, 115, 26}, {116, 116, 27}, {117, 117, 19}, {118, 118, 28}, {119, 119, 29}, {120, 122, 19}, },
            {},
            {},
            {},
            {},
            {{32, 32, 30}, {33, 33, 31}, {34, 34, 32}, {35, 35, 33}, {36, 36, 34}, {37, 37, 35}, {38, 38, 36}, {39, 39, 37}, {40, 40, 38}, {41, 41, 39}, {42, 42, 40}, {43, 43, 41}, {44, 44, 42}, {45, 45, 43}, {46, 46, 44}, {47, 47, 45}, {48, 57, 46}, {58, 58, 47}, {59, 59, 48}, {60, 60, 49}, {61, 61, 50}, {62, 62, 51}, {63, 63, 52}, {64, 64, 53}, {65, 90, 54}, {91, 91, 55}, {92, 92, 56}, {93, 93, 57}, {94, 94, 58}, {95, 95, 59}, {96, 96, 60}, {97, 122, 61}, {123, 123, 62}, {124, 124, 63}, {125, 125, 64}, {126, 126, 65}, },
            {},
            {},
            {},
            {},
            {},
            {{47, 47, 66}, },
            {{46, 46, 67}, },
            {{46, 46, 67}, {48, 57, 68}, },
            {},
            {},
            {},
            {{48, 57, 69}, {65, 90, 70}, {95, 95, 71}, {97, 122, 72}, },
            {{48, 122, -19}, },
            {{48, 122, -19}, },
            {{48, 95, -19}, {97, 110, 72}, {111, 111, 73}, {112, 122, 72}, },
            {{48, 95, -19}, {97, 107, 72}, {108, 108, 74}, {109, 109, 72}, {110, 110, 75}, {111, 122, 72}, },
            {{48, 107, -23}, {108, 108, 76}, {109, 122, 72}, },
            {{48, 95, -19}, {97, 101, 72}, {102, 102, 77}, {103, 109, 72}, {110, 110, 78}, {111, 122, 72}, },
            {{48, 95, -19}, {97, 113, 72}, {114, 114, 79}, {115, 122, 72}, },
            {{48, 95, -19}, {97, 100, 72}, {101, 101, 80}, {102, 122, 72}, },
            {{48, 95, -19}, {97, 115, 72}, {116, 116, 81}, {117, 122, 72}, },
            {{48, 95, -19}, {97, 103, 72}, {104, 104, 82}, {105, 122, 72}, },
            {{48, 95, -19}, {97, 97, 83}, {98, 122, 72}, },
            {{48, 103, -29}, {104, 104, 84}, {105, 122, 72}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {},
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{34, 34, 85}, {92, 92, 86}, {97, 97, 87}, {98, 98, 88}, {102, 102, 89}, {110, 110, 90}, {114, 114, 91}, {116, 116, 92}, {118, 118, 93}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{0, 9, 94}, {11, 65535, 95}, },
            {{48, 57, 96}, },
            {{46, 57, -15}, },
            {{48, 122, -19}, },
            {{48, 122, -19}, },
            {{48, 122, -19}, },
            {{48, 122, -19}, },
            {{48, 95, -19}, {97, 109, 72}, {110, 110, 97}, {111, 122, 72}, },
            {{48, 95, -19}, {97, 114, 72}, {115, 115, 98}, {116, 122, 72}, },
            {{48, 95, -19}, {97, 99, 72}, {100, 100, 99}, {101, 122, 72}, },
            {{48, 110, -22}, {111, 111, 100}, {112, 122, 72}, },
            {{48, 122, -19}, },
            {{48, 115, -28}, {116, 116, 101}, {117, 122, 72}, },
            {{48, 95, -19}, {97, 104, 72}, {105, 105, 102}, {106, 122, 72}, },
            {{48, 95, -19}, {97, 97, 103}, {98, 122, 72}, },
            {{48, 113, -26}, {114, 114, 104}, {115, 122, 72}, },
            {{48, 100, -27}, {101, 101, 105}, {102, 122, 72}, },
            {{48, 113, -26}, {114, 114, 106}, {115, 122, 72}, },
            {{48, 104, -81}, {105, 105, 107}, {106, 122, 72}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{32, 126, -7}, },
            {{0, 65535, -68}, },
            {{0, 65535, -68}, },
            {{48, 57, 96}, },
            {{48, 100, -27}, {101, 101, 108}, {102, 122, 72}, },
            {{48, 100, -27}, {101, 101, 109}, {102, 122, 72}, },
            {{48, 104, -81}, {105, 105, 110}, {106, 122, 72}, },
            {{48, 95, -19}, {97, 97, 111}, {98, 122, 72}, },
            {{48, 122, -19}, },
            {{48, 109, -75}, {110, 110, 112}, {111, 122, 72}, },
            {{48, 99, -77}, {100, 100, 113}, {101, 122, 72}, },
            {{48, 104, -81}, {105, 105, 114}, {106, 122, 72}, },
            {{48, 109, -75}, {110, 110, 115}, {111, 122, 72}, },
            {{48, 122, -19}, },
            {{48, 107, -23}, {108, 108, 116}, {109, 122, 72}, },
            {{48, 122, -19}, },
            {{48, 122, -19}, },
            {{48, 101, -25}, {102, 102, 117}, {103, 122, 72}, },
            {{48, 115, -28}, {116, 116, 118}, {117, 122, 72}, },
            {{48, 115, -28}, {116, 116, 119}, {117, 122, 72}, },
            {{48, 122, -19}, },
            {{48, 109, -75}, {110, 110, 120}, {111, 122, 72}, },
            {{48, 122, -19}, },
            {{48, 100, -27}, {101, 101, 121}, {102, 122, 72}, },
            {{48, 122, -19}, },
            {{48, 122, -19}, },
            {{48, 122, -19}, },
            {{48, 95, -19}, {97, 102, 72}, {103, 103, 122}, {104, 122, 72}, },
            {{48, 122, -19}, },
            {{48, 122, -19}, },
        }
    };*/

    private static int[][] accept;
/*  {
        // INITIAL
        {-1, 0, 0, 0, 0, -1, 6, 7, 3, 1, 2, 4, 23, 23, 9, 5, 8, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, -1, -1, 25, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 20, -1, 23, 22, 22, 22, 22, 14, 22, 22, 22, 16, 22, 22, 22, 22, 22, 22, 22, -1, -1, -1, -1, -1, -1, -1, -1, -1, 20, 20, 24, 22, 22, 22, 22, 21, 22, 22, 22, 22, 10, 22, 15, 18, 22, 22, 22, 11, 22, 17, 22, 19, 21, 12, 22, 13, 21, },

    };*/

    public static class State
    {
        public final static State INITIAL = new State(0);

        private int id;

        private State(@SuppressWarnings("hiding") int id)
        {
            this.id = id;
        }

        public int id()
        {
            return this.id;
        }
    }

    static 
    {
        try
        {
            DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                Lexer.class.getResourceAsStream("lexer.dat")));

            // read gotoTable
            int length = s.readInt();
            gotoTable = new int[length][][][];
            for(int i = 0; i < gotoTable.length; i++)
            {
                length = s.readInt();
                gotoTable[i] = new int[length][][];
                for(int j = 0; j < gotoTable[i].length; j++)
                {
                    length = s.readInt();
                    gotoTable[i][j] = new int[length][3];
                    for(int k = 0; k < gotoTable[i][j].length; k++)
                    {
                        for(int l = 0; l < 3; l++)
                        {
                            gotoTable[i][j][k][l] = s.readInt();
                        }
                    }
                }
            }

            // read accept
            length = s.readInt();
            accept = new int[length][];
            for(int i = 0; i < accept.length; i++)
            {
                length = s.readInt();
                accept[i] = new int[length];
                for(int j = 0; j < accept[i].length; j++)
                {
                    accept[i][j] = s.readInt();
                }
            }

            s.close();
        }
        catch(Exception e)
        {
            throw new RuntimeException("The file \"lexer.dat\" is either missing or corrupted.");
        }
    }
}
