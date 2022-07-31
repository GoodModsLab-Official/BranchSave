package com.HiSoft.BranchSaveIDE;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import java.io.*;
import java.text.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.*;
import org.json.*;

public class MainActivity extends Activity {
	
	public final int REQ_CD_FILEPICK = 101;
	
	private Timer _timer = new Timer();
	
	private double n = 0;
	private String code = "";
	private boolean isNextToken = false;
	private HashMap<String, Object> mapJson = new HashMap<>();
	private String keyBuffer = "";
	private String valueBuffer = "";
	
	private ArrayList<String> tokenList = new ArrayList<>();
	
	private LinearLayout background_;
	private TextView text_title_app_;
	private EditText code_area_;
	private TextView text_logger_;
	private LinearLayout box_compile_button_;
	private Button btn_file_open_;
	private Button btn_compiler_json_;
	
	private TimerTask time;
	private Intent filePick = new Intent(Intent.ACTION_GET_CONTENT);
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		
		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
			||checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
				requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
			} else {
				initializeLogic();
			}
		} else {
			initializeLogic();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		background_ = findViewById(R.id.background_);
		text_title_app_ = findViewById(R.id.text_title_app_);
		code_area_ = findViewById(R.id.code_area_);
		text_logger_ = findViewById(R.id.text_logger_);
		box_compile_button_ = findViewById(R.id.box_compile_button_);
		btn_file_open_ = findViewById(R.id.btn_file_open_);
		btn_compiler_json_ = findViewById(R.id.btn_compiler_json_);
		filePick.setType("*/*");
		filePick.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		
		btn_file_open_.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				startActivityForResult(filePick, REQ_CD_FILEPICK);
			}
		});
		
		btn_compiler_json_.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				text_logger_.setText("Successfully!");
				isNextToken = true;
				_lib_BranchSave(code_area_.getText().toString(), 0);
				time = new TimerTask() {
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								text_logger_.setText("");
							}
						});
					}
				};
				_timer.schedule(time, (int)(5000));
			}
		});
	}
	
	private void initializeLogic() {
		// Copyrigth (C) 2022 by HiSoft. All rights reserved.
		// This application source code can be used based on MIT license
		
		if (android.os.Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP){
			getWindow().setNavigationBarColor(Color.parseColor("#00B38B"));
		}
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);
		
		switch (_requestCode) {
			case REQ_CD_FILEPICK:
			if (_resultCode == Activity.RESULT_OK) {
				ArrayList<String> _filePath = new ArrayList<>();
				if (_data != null) {
					if (_data.getClipData() != null) {
						for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
							ClipData.Item _item = _data.getClipData().getItemAt(_index);
							_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
						}
					}
					else {
						_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
					}
				}
				if (!_filePath.get((int)(0)).endsWith(".brch")) {
					SketchwareUtil.showMessage(getApplicationContext(), "This is not a branch file");
				}
				else {
					code_area_.setText(FileUtil.readFile(_filePath.get((int)(0))));
					SketchwareUtil.showMessage(getApplicationContext(), "Successfully!");
				}
			}
			else {
				
			}
			break;
			default:
			break;
		}
	}
	
	public void _lib_BranchSave(final String _context, final double _n) {
		try {
			n = _n;
			code = _context.trim();
			tokenList.addAll(Arrays.asList(code.split(";")));
			for(int _repeat16 = 0; _repeat16 < (int)(tokenList.size()); _repeat16++) {
				if (n > tokenList.size()) {
					text_logger_.setText("ERROR: ".concat(_lib_BranchSave_ErrorModule("parser")));
				}
				else {
					_lib_BranchSave_Parser(tokenList.get((int)(n)));
					if (isNextToken) {
						n++;
					}
				}
			}
			if (!mapJson.containsKey("FILE")) {
				FileUtil.writeFile(FileUtil.getExternalStorageDir().concat("/BranchSave/file.brch"), new Gson().toJson(mapJson));
			}
			else {
				FileUtil.writeFile(FileUtil.getExternalStorageDir().concat(mapJson.get("FILE").toString()), new Gson().toJson(mapJson));
			}
		} catch(Exception e) {
			text_logger_.setText("ERROR: ".concat(String.valueOf(e).replace("java", "BranchSave")));
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
		if (!(Sourse__[0].trim().equals("FILE") || Sourse__[1].trim().contains("(read)"))) {
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
				                if (pos < _context.length()) throw new RuntimeException("Unexpected: " + (char)ch + " line " + pos);
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
			valueBuffer = FileUtil.readFile(FileUtil.getExternalStorageDir().concat(_context.replace("(read)", "")));
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
		if (_context.contains("(trim)")) {
			valueBuffer = _context.replace("(trim)", "").trim();
		}
	}
	
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}