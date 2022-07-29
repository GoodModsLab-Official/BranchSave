package com.HiSoft.BranchSave;

import com.HiSoft.BranchSave.libBranchSave;
import com.google.gson.Gson;
import java.io.*;
import java.text.*;
import android.util.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.*;
import org.json.*;

public class main {
	private double n = 0;
	private String code = "";
	private boolean isNextToken = false;
	private HashMap<String, Object> mapJson = new HashMap<>();
	private String keyBuffer = "";
	private String valueBuffer = "";

	
	private ArrayList<String> tokenList = new ArrayList<>();
	
  public static void main(String[] args) {
   System.out.println("BranchSave v.0.1.0");
 
 //  _lib_BranchSave("FILE:/BranchSave/test.json; key:1;", 0);

	}	
	
///////////////////////////Test zone////////////////
	public void _lib_BranchSave(final String _context, final double _n) {
		try {
			n = _n;
			code = _context.trim();
			tokenList.addAll(Arrays.asList(code.split(";")));
			for(int _repeat16 = 0; _repeat16 < (int)(tokenList.size()); _repeat16++) {
				if (n > tokenList.size()) {
					System.out.println("ERROR: ".concat(_lib_BranchSave_ErrorModule("parser")));
				}
				else {
					_lib_BranchSave_Parser(tokenList.get((int)(n)));
					if (isNextToken) {
						n++;
					}
				}
			}
			if (!mapJson.containsKey("FILE")) {
				System.out.println("ERROR: ".concat(_lib_BranchSave_ErrorModule("file")));
			}
			else {
				FileUtil.writeFile(FileUtil.getExternalStorageDir().concat(mapJson.get("FILE").toString()), new Gson().toJson(mapJson));
			}
		} catch(Exception e) {
			System.out.println("ERROR: ".concat(String.valueOf(e).replace("java", "BranchSave")));
		}
	}
	
	
	public String _lib_BranchSave_ErrorModule(final String _code) {
		switch(_code){
			
			case "parser":
			return "BranchSave.lang.Parser: The position of the tokens is greater than the length of the array";
			
			case "file":
			return "BranchSave.lang.Files: Missing key before file path";
			
			default:
			return "Code finished, type 0";
		}
	}
	



	public void _lib_BranchSave_Parser(final String _context) {
		String Sourse_ = _context.trim();
		String[] Sourse__ = Sourse_.split("\\:");
		
		keyBuffer = Sourse__[0].trim();
		valueBuffer = Sourse__[1].trim();
		if (!Sourse__[0].trim().equals("FILE")) {
			if (Sourse__[0].trim().contains("+") || (Sourse__[0].trim().contains("-") || (Sourse__[0].trim().contains("/") || Sourse__[0].trim().contains("*")))) {
				keyBuffer = String.valueOf(_lib_BranchSave_Eval(Sourse__[0].trim()));
			}
			if (Sourse__[1].trim().contains("+") || (Sourse__[1].trim().contains("-") || (Sourse__[1].trim().contains("/") || Sourse__[1].trim().contains("*")))) {
				valueBuffer = String.valueOf(_lib_BranchSave_Eval(Sourse__[1].trim()));
			}
		}
		if (mapJson.containsKey(Sourse__[1].trim())) {
			_lib_BranchSave_ParserTerminal(Sourse__[1].trim());
			mapJson.put(keyBuffer, valueBuffer.replace(Sourse__[1].trim(), mapJson.get(Sourse__[1].trim()).toString()));
		}
		else {
			_lib_BranchSave_ParserTerminal(valueBuffer);
			mapJson.put(keyBuffer, valueBuffer);
		}
	}
	
	
	public double _lib_BranchSave_Eval(final String _context) {
		        return new Object() {
			            int pos = -1, ch;
			
			            void nextChar() {
				                ch = (++pos < _context.length()) ? _context.charAt(pos) : -1;
				            }
			
			            boolean eat(int charToEat) {
				                while (ch == ' ') nextChar();
				                if (ch == charToEat) {
					                    nextChar();
					                    return true;
					                }
				                return false;
				            }
			
			            double parse() {
				                nextChar();
				                double x = parseExpression();
				                if (pos < _context.length()) throw new RuntimeException("Unexpected: " + (char)ch);
				                return x;
				            }
			
			            // Grammar:
			            // expression = term | expression `+` term | expression `-` term
			            // term = factor | term `*` factor | term `/` factor
			            // factor = `+` factor | `-` factor | `(` expression `)` | number
			            //        | functionName `(` expression `)` | functionName factor
			            //        | factor `^` factor
			
			            double parseExpression() {
				                double x = parseTerm();
				                for (;;) {
					                    if      (eat('+')) x += parseTerm(); // addition
					                    else if (eat('-')) x -= parseTerm(); // subtraction
					                    else return x;
					                }
				            }
			
			            double parseTerm() {
				                double x = parseFactor();
				                for (;;) {
					                    if      (eat('*')) x *= parseFactor(); // multiplication
					                    else if (eat('/')) x /= parseFactor(); // division
					                    else return x;
					                }
				            }
			
			            double parseFactor() {
				                if (eat('+')) return +parseFactor(); // unary plus
				                if (eat('-')) return -parseFactor(); // unary minus
				
				                double x;
				                int startPos = this.pos;
				                if (eat('(')) { // parentheses
					                    x = parseExpression();
					                    if (!eat(')')) throw new RuntimeException("Missing ')'");
					                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
					                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
					                    x = Double.parseDouble(_context.substring(startPos, this.pos));
					                } else if (ch >= 'a' && ch <= 'z') { // functions
					                    while (ch >= 'a' && ch <= 'z') nextChar();
					                    String func = _context.substring(startPos, this.pos);
					                    if (eat('(')) {
						                        x = parseExpression();
						                        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
						                    } else {
						                        x = parseFactor();
						                    }
					                    if (func.equals("sqrt")) x = Math.sqrt(x);
					                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
					                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
					                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
					                    else throw new RuntimeException("Unknown function: " + func);
					                } else {
					                    throw new RuntimeException("Unexpected: " + (char)ch);
					                }
				
				                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
				
				                return x;
				            }
			        }.parse();
	}
	
	
	public void _lib_BranchSave_ParserTerminal(final String _context) {
		if (_context.contains("(len)")) {
			valueBuffer = String.valueOf((long)(_context.replace("(len)", "").length()));
		}
		if (_context.contains("(read)")) {
			valueBuffer = FileUtil.readFile(_context.replace("(read)", ""));
		}
		if (_context.contains("(rand)")) {
			valueBuffer = String.valueOf(SketchwareUtil.getRandom((int)(0), (int)(Double.parseDouble(_context.replace("(rand)", "")))));
		}
		if (_context.contains("(lowcase)")) {
			valueBuffer = _context.replace("(lowcase)", "").toLowerCase();
		}
		if (_context.contains("(upcase)")) {
			valueBuffer = _context.replace("(lowcase)", "").toUpperCase();
		}
		if (_context.contains("(upcase)")) {
			valueBuffer = _context.replace("(upcase)", "").toUpperCase();
		}
	}
	

	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
}
