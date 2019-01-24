import java.util.*;
import java.util.Vector;
import java.io.*;

public class GenByteCode {

	private String module;

	private Vector<Symbol> stack ;
	private Vector<Symbol> origstack;

	private ArrayList<Symbol> localVars;
	private ArrayList<Symbol> globalVars= new ArrayList<Symbol>();
	private int NoJumps;
	private int NoTempVariables;

	private Vector<String> lines = new Vector<String>();
	private ArrayList<Symbol> functions = new ArrayList<Symbol>();

	public GenByteCode(Vector<Symbol> stack){
		this.origstack=stack;
		this.stack=(Vector<Symbol>)stack.clone();
		changeStackToLowerCase();
		this.localVars=new ArrayList<Symbol>();
		this.NoJumps=0;
		this.NoTempVariables=0;
		buildLines();
	}

	public void printLines(){
		for(int i = 0; i < lines.size(); i++){
        	System.out.println(lines.get(i));
      	}
	}

	public void writeToFile(String filename){
		try {
			String current = new File( "." ).getCanonicalPath();
			File file = new File(current + "/output/" + filename);

			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			for(int i = 0; i < lines.size(); i++){
						writer.write(lines.get(i));
						writer.write("\r\n");
			}
			writer.close();

		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isInteger(String s) {
	    try {
	        Integer.parseInt(s);
	    } catch(NumberFormatException e) {
	        return false;
	    } catch(NullPointerException e) {
	        return false;
	    }
	    // only got here if we didn't return false
	    return true;
	}

	public void printIR(){
		for(int i = 0; i < stack.size(); i++){
        	System.out.println(stack.get(i).getType()+": "+stack.get(i).getName());
    }
	}

	public void buildFunctionArray() {
		for(int i = 0; i < stack.size(); i++) {
				if(stack.get(i).getType().equals("function")) {
					int j = i+1;
					while(!stack.get(j).getType().contains("returnType")) {
						j++;
					}
					if(stack.get(j).getType().equals("returnTypeA"))
						functions.add(new Symbol(stack.get(i).getName(), 1, stack.get(j).getType()));
					else if(stack.get(j).getType().equals("returnTypeS"))
						functions.add(new Symbol(stack.get(i).getName(), 0, stack.get(j).getType()));
					else
						functions.add(new Symbol(stack.get(i).getName(), 0, "void"));
				}
		}
	}

	public Vector<String> checkForIincs(Vector<String> stmtLines) {
		for(int i = 0; i < stmtLines.size()-2; i++) {
			if(stmtLines.get(i).contains("iload") && stmtLines.get(i+1).contains("iconst_1") && stmtLines.get(i+2).contains("iadd") && stmtLines.get(i+3).contains("istore")) {
				String[] iload_var = stmtLines.get(i).split("_");
				if(iload_var.length<2) {
					iload_var = stmtLines.get(i).split(" ");
				}
				String[] istore_var = stmtLines.get(i+3).split("_");
				if(istore_var.length<2) {
					istore_var = stmtLines.get(i+3).split(" ");
				}
				if(istore_var[1].equals(iload_var[1])) {
					System.out.println("IINC!!!!!!");
					stmtLines.remove(i+3);
					stmtLines.remove(i+2);
					stmtLines.remove(i+1);
					stmtLines.remove(i);
					stmtLines.add(i, "iinc " + istore_var[1] + " " + 1);
				}
			}
		}
		return stmtLines;
	}

	public void buildLines(){

		Vector <String> endingBlockLines = new Vector<String>();
		int endingBlockLocals=0;

		if(stack.get(0).getType().equals("module")){
			module=stack.get(0).getName();
			lines.add(".class public " + stack.get(0).getName());
			lines.add(".super java/lang/Object");
			stack.remove(0);
		}
		else{
			System.out.println("Error: Module doesn't exist in stack!!");
			return;
		}

		buildFunctionArray();

		while(stack.size()>0){

			if(stack.get(0).getType().equals("function")){
				String funcName = stack.get(0).getName();
				//System.out.println(funcName+">>>>>>>>>>>>Calculated Stack: "+CalculateStackSize());
				stack.remove(0);

				int funcArg;
				if(stack.get(0).getType()=="nrOfArgs") {
					funcArg=Integer.parseInt(stack.get(0).getName());
					stack.remove(0);
				}else {
					funcArg=0;
				}

				Symbol[] args = new Symbol[funcArg];
				for(int i=0; i<funcArg; i++){
					args[i]=stack.get(0);
					stack.remove(0);
				}

				Symbol funcReturn;
				if(stack.get(0).getType().contains("returnType")){
					funcReturn=stack.get(0);
					stack.remove(0);
				}else{
					System.out.println("Return Value not specified for function" + funcName);
					return;
				}

				String tempLine;
				tempLine=".method public static "+funcName+"(";
				if(funcName.equals("main")) {
					tempLine+="[Ljava/lang/String;";

				}else {
					for(int i=0; i<funcArg; i++){
						if(args[i].getType().equals("funcDeclArg")){
							tempLine+="I";
						}else{
							tempLine+="[I";
						}
					}
				}
				tempLine+=")";
				if(funcReturn.getName().equals("void")){
					tempLine+="V";
				}else if(funcReturn.getType().equals("returnTypeA")){
					tempLine+="[I";
				}else{
					tempLine+="I";
				}
				lines.add(tempLine);

				Vector<String> Stmtlines = BuildFunctionLines(args, funcReturn);
				int locals = this.localVars.size();
				if(funcName.equals("main") && this.localVars.size()==0)
					locals = 1;

				checkForIincs(Stmtlines);
				int stack = getStackLimit(Stmtlines);
				if(args.length > stack)
					stack = args.length;
				System.out.println("CHECK STACK: " +stack);
				lines.add(".limit locals "+locals);
				lines.add(".limit stack " +stack);

				for(int i=0; i<Stmtlines.size(); i++) {
					lines.add(Stmtlines.get(i));
				}
				if(!lines.get(lines.size()-1).contains("return"))
					lines.add("return");
				lines.add(".end method");
				lines.add("");
				lines.add("");
				this.localVars.clear();
			}
			else if (stack.get(0).getType().equals("global")){
				Symbol globalVar=stack.get(0);
				System.out.println(globalVar.getName());
				stack.remove(0);
				if(globalVar.getScalarOrArray()==0) {
					lines.add(".field static "+globalVar.getName()+" I");
				}else {
					lines.add(".field static "+globalVar.getName()+" [I");
				}
				this.globalVars.add(globalVar);
			}else if(stack.get(0).getType().equals("arraySizeC")) {
				if(Integer.parseInt(stack.get(0).getName()) <= 5 && Integer.parseInt(stack.get(0).getName()) >= 0)
					endingBlockLines.add("iconst_"+Integer.parseInt(stack.get(0).getName()));
				else
					endingBlockLines.add("bipush "+Integer.parseInt(stack.get(0).getName()));
				stack.remove(0);
				endingBlockLocals++;
				endingBlockLines.add("newarray int");
				endingBlockLines.add(StoreGlobalVariable(stack.get(0)));
			}
		}

		//ending standart block
		lines.add(".method static public <clinit>()V");
		lines.add(".limit stack "+getStackLimit(endingBlockLines));
		lines.add(".limit locals "+endingBlockLocals);
		lines.addAll(lines.size(), endingBlockLines);
		lines.add("return");
		lines.add(".end method");
	}

	public String StoreGlobalVariable(Symbol var) {
		int pos= VariableIsGlobal(var);
		int scalarOrArray;
		if(pos==-1) {
			scalarOrArray=var.getScalarOrArray();
		}else {
			scalarOrArray=this.globalVars.get(pos).getScalarOrArray();
		}
		if(scalarOrArray==0) {
			return ("putstatic "+this.module+"/"+var.getName()+" I");
		}else {
			return ("putstatic "+this.module+"/"+var.getName()+" [I");
		}
	}

	public String LoadGlobalVariable(Symbol var) {
		int pos= VariableIsGlobal(var);
		int scalarOrArray;
		if(pos==-1) {
			scalarOrArray=var.getScalarOrArray();
		}else {
			scalarOrArray=this.globalVars.get(pos).getScalarOrArray();
		}

		if(scalarOrArray==0) {
			return ("getstatic "+this.module+"/"+var.getName()+" I");
		}else {
			return ("getstatic "+this.module+"/"+var.getName()+" [I");
		}

	}

	public int VariableIsGlobal(Symbol var) {
		for(int i=0; i<this.globalVars.size(); i++) {
			if(this.globalVars.get(i).getName().equals(var.getName())) {
				return i;
			}
		}
		return -1;
	}

	public int VariableIsAlreadyDeclared(Symbol var) {
		for(int i=0; i<this.localVars.size(); i++) {
			if(this.localVars.get(i).getName().equals(var.getName())) {
				return i;
			}
		}
		return -1;
	}

	public boolean VariableIsScalar(Symbol var) {
		for(int i=0; i<this.localVars.size(); i++) {
			if(this.localVars.get(i).getName().equals(var.getName())) {
				if(this.localVars.get(i).getScalarOrArray()==0)
					return true;
				else
					return false;
			}
		}

		for(int i=0; i<this.globalVars.size(); i++) {
			if(this.globalVars.get(i).getName().equals(var.getName())) {
				if(this.globalVars.get(i).getScalarOrArray()==0)
					return true;
				else
					return false;
			}
		}
		return true;
	}

	public int getFunctionReturn(String name) {
		for(int i=0; i<this.functions.size(); i++) {
			if(this.functions.get(i).getName().equals(name)) {
				if(this.functions.get(i).getType().equals("void"))
					return -1;
				else if(this.functions.get(i).getType().equals("returnTypeS"))
					return 0;
				else
					return 1;
			}
		}
		return 1;
	}

	public Vector<String> BuildFunctionLines(Symbol args[], Symbol funcReturn) {
		for(int i=args.length-1; i>=0; i--) {
			localVars.add(args[i]);
		}

		Vector<String> stmtLines = new Vector<String>();
		Vector<String> tempStmtLines = new Vector<String>();

		stmtLines.addAll(stmtLines.size(), BuildSmtLines());
		if(!funcReturn.getType().equals("returnType")){
			if(funcReturn.getType().equals("returnTypeS")) {
				if(VariableIsAlreadyDeclared(funcReturn) == -1) {
					localVars.add(funcReturn);
					stmtLines.add(0,tratar("istore", localVars.size()-1));
					stmtLines.add(0,"iconst_0");
				}
				else {
					boolean b = true;
					for(int i = 0; i < args.length; i++) {
						if(args[i].getName().equals(funcReturn.getName())) {
							b = false;
						}
					}
					if(b) {
						stmtLines.add(0,tratar("istore", VariableIsAlreadyDeclared(funcReturn)));
						stmtLines.add(0,"iconst_0");
					}
				}
			}
			else {
					localVars.add(new Symbol(funcReturn.getName(), 1, funcReturn.getType()));
					stmtLines.add(0,tratar("astore", localVars.size()-1));
					stmtLines.add(0,"newarray int");
					stmtLines.add(0,"iconst_0");
			}
		}


		stmtLines.add("");
		if(funcReturn.getName()!="void") {
			int pos=VariableIsAlreadyDeclared(funcReturn);
			if(pos!=-1 && VariableIsScalar(funcReturn)) {
				stmtLines.add(tratar("iload",pos));
				stmtLines.add("ireturn");
			}
			else if(pos!=-1 && !VariableIsScalar(funcReturn)) {
				stmtLines.add(tratar("aload",pos));
				stmtLines.add("areturn");
			}
		}

		return stmtLines;
	}

	public Vector<String> BuildSmtLines(){

		Vector<String> stmtLines = new Vector<String>();

		while(stack.size()>0){
			Symbol stackSymbol0=stack.get(0);
			Symbol stackSymbol2;
			if(stack.size()>2) {
				stackSymbol2=stack.get(2);
			}else {
				stackSymbol2=new Symbol("",0,"");
			}

			if(stackSymbol0.getType().equals("function")){
		 		break;
			}


			if(stackSymbol0.getType().equals("if_else")) {
				stmtLines.addAll(stmtLines.size(), Build_If_Else_Lines());
				continue;
			}

			if(stackSymbol0.getType().equals("if")) {
				stmtLines.addAll(stmtLines.size(), Build_If_Lines());
				continue;
			}

			if(stackSymbol0.getType().equals("while")) {
				stmtLines.addAll(stmtLines.size(), Build_While_Lines());
				continue;
			}

			if(stackSymbol0.getType().equals("end_while") || stackSymbol0.getType().equals("end_if") || stackSymbol0.getType().equals("else")) {
				break;
			}

			if(stackSymbol0.getType().equals("funcInvokeArg")){
				Vector<Symbol> callArgs = new Vector<Symbol>();
				while(stackSymbol0.getType().equals("funcInvokeArg")) {
					callArgs.add(stackSymbol0);
					stack.remove(0);
					stackSymbol0=stack.get(0);
				}

				stackSymbol0=stack.get(0);
				stack.remove(0);
				String funcName="";
				if(stackSymbol0.getType().equals("callFunc")) {
					funcName = stackSymbol0.getName();
				}else {
					System.out.println("Something went Wrong!!!");
				}

				for(int i=0; i<callArgs.size(); i++) {
					if(callArgs.get(i).getName().contains("\"")) {
						stmtLines.add("ldc "+callArgs.get(i).getName());
					}else if(!isInteger(callArgs.get(i).getName())) {
						int pos=VariableIsAlreadyDeclared(callArgs.get(i));
						if(pos != -1) {
							if(VariableIsScalar(callArgs.get(i)))
								stmtLines.add(tratar("iload", pos));
							else
								stmtLines.add(tratar("aload", pos));
						}
						pos=VariableIsGlobal(callArgs.get(i));
						if(pos!= -1) {
							stmtLines.add(LoadGlobalVariable(callArgs.get(i)));
						}
					}else if(isInteger(callArgs.get(i).getName())){
						if(Integer.parseInt(callArgs.get(i).getName()) <= 5 && Integer.parseInt(callArgs.get(i).getName()) >= 0)
							stmtLines.add("iconst_"+Integer.parseInt(callArgs.get(i).getName()));
						else
							stmtLines.add("bipush "+Integer.parseInt(callArgs.get(i).getName()));
					}else{
						System.out.println("Something went Wrog!!!!!3    x(");
						return stmtLines;
					}
				}

				String tempLine;
				if(funcName.contains(".")){
					String[] split = funcName.split("\\.");
					tempLine="invokestatic " + split[0] + "/" + split[1] + "(";
					for(int i=0; i<callArgs.size(); i++){
						if(callArgs.get(i).getName().contains("\"")) {
							tempLine+="Ljava/lang/String;";
						}else if(VariableIsScalar(callArgs.get(i))) {
								tempLine+="I";
						}else {
								tempLine+="[I";
						}
					}
					tempLine+=")";
					if(stack.size()>0 && stack.get(1).getName().equals("=")) {
						if(VariableIsScalar(stack.get(0))){
							tempLine+="I";
						}else{
							tempLine+="[I";
						}

					}else {
						tempLine+="V";
					}
				}else {
					tempLine="invokestatic "+module+"/"+funcName+"(";

					for(int i=0; i<callArgs.size(); i++){
						if(callArgs.get(i).getName().contains("\"")) {
							tempLine+="Ljava/lang/String;";
						}else if(VariableIsScalar(callArgs.get(i))) {
								tempLine+="I";
						}else {
								tempLine+="[I";
						}
					}
					tempLine+=")";
					System.out.println("funcName1: " + funcName);
					if(stack.size()>0 && stack.get(1).getName().equals("=")) {
						if(getFunctionReturn(funcName) == 0){
							tempLine+="I";
						}else{
							tempLine+="[I";
						}


					}else {
						tempLine+="V";
					}
				}

				stmtLines.add(tempLine);


				if(stack.size() > 2){
				 if(stack.get(1).getName().equals("=")){
					if(stack.get(0).getType().equals("variable")){
						int pos=VariableIsAlreadyDeclared(stack.get(0));
						int globalpos=VariableIsGlobal(stack.get(0));

						if(pos!=-1){
							if(VariableIsScalar(stack.get(0)))
								stmtLines.add(tratar("istore", pos));
							else
								stmtLines.add(tratar("astore", pos));
						}else if(globalpos!=-1) {
							stmtLines.add(StoreGlobalVariable(stack.get(0)));
						}else{
							if(stmtLines.get(stmtLines.size()-1).contains("invokestatic")) {
								String invokestatic = stmtLines.get(stmtLines.size()-1);
								String[] split = invokestatic.split("\\)");
								String returnType = split[1];
								if(returnType.equals("I")) {
									stmtLines.add(tratar("istore", this.localVars.size()));
									this.localVars.add(stack.get(0));
								}
								else {
									stmtLines.add(tratar("astore", this.localVars.size()));
									this.localVars.add(new Symbol(stack.get(0).getName(), 1, stack.get(0).getType()));
								}
							}

						}
						stack.remove(0);
						stack.remove(0);
					}
				 }
				}else if(stack.size() > 1){
					 	if(stack.get(0).getType().equals("arrayAccess")) {
							int pos=VariableIsAlreadyDeclared(stack.get(0));
							int globalpos=VariableIsGlobal(stack.get(0));
							if(globalpos!=-1) {
								stmtLines.add(LoadGlobalVariable(stack.get(0)));
							}else {
								stmtLines.add(tratar("aload", pos));
							}
							stack.remove(0);
							pos=VariableIsAlreadyDeclared(stack.get(0));
							globalpos=VariableIsGlobal(stack.get(0));
							if(globalpos!=-1) {
								stmtLines.add(LoadGlobalVariable(stack.get(0)));
							}else	{
								stmtLines.add(tratar("iload", pos));
							}
							stack.remove(0);
							stmtLines.add("iastore");
						}
				 }
				continue;
			}


			stmtLines.addAll(stmtLines.size(), Build_Aritmetic_Lines());

		}

		return stmtLines;
	}

	public Vector<String> Build_If_Lines(){
		Vector<String> stmtLines = new Vector<String>();
		stack.remove(0);
		int jumpN=this.NoJumps;

		stmtLines.addAll(stmtLines.size(), this.Build_ConditionExp_Lines());
		NoJumps++;

		stmtLines.addAll(stmtLines.size(), this.BuildSmtLines());

		stmtLines.add("loop"+jumpN+"_end:");
		stack.remove(0);


		return stmtLines;
	}

	public Vector<String> Build_If_Else_Lines(){
		Vector<String> stmtLines = new Vector<String>();
		stack.remove(0);
		int jumpN=this.NoJumps;
		stmtLines.addAll(stmtLines.size(), this.Build_ConditionExp_Lines());
		NoJumps++;

		//If Statement
		stmtLines.addAll(stmtLines.size(), this.BuildSmtLines());
		stmtLines.add("");
		stmtLines.add("goto loop"+jumpN+"_next");
		stack.remove(0);

		//Else Statement
		stmtLines.add("loop"+jumpN+"_end:");
		stmtLines.addAll(stmtLines.size(), this.BuildSmtLines());

		stmtLines.add("");
		stmtLines.add("loop"+jumpN+"_next:");
		stack.remove(0);

		return stmtLines;
	}

	public Vector<String> Build_While_Lines(){
		Vector<String> stmtLines = new Vector<String>();
		stack.remove(0);
		int jumpN=this.NoJumps;

		stmtLines.add("loop"+jumpN+":");

		stmtLines.addAll(stmtLines.size(), this.Build_ConditionExp_Lines());
		NoJumps++;

		stmtLines.addAll(stmtLines.size(), this.BuildSmtLines());

		stmtLines.add("goto loop"+jumpN);
		stmtLines.add("");
		stmtLines.add("loop"+jumpN+"_end:");
		stack.remove(0);


		return stmtLines;
	}

	public Vector<String> Build_ConditionExp_Lines(){
		Vector<String> stmtLines = new Vector<String>();
		Vector<String> tempStmtLines = new Vector<String>();
		Symbol stackSymbol0=stack.get(0);
		Symbol firstMember=stack.get(0);
		Symbol secondMember=stack.get(1);
		boolean referenceFlag=false;

		if(firstMember.getType().equals("arrayAccess") && secondMember.getType().equals("arrayAccess") && stack.get(2).getType().equals("condicOp")) {
			referenceFlag=true;
		}

		if(stackSymbol0.getType().equals("variable")) {
			int pos=VariableIsAlreadyDeclared(stack.get(0));
			int globalpos=VariableIsGlobal(stack.get(0));

			if(pos!=-1)
				tempStmtLines.add(tratar("iload", pos));
			else if(globalpos!=-1)
				tempStmtLines.add(LoadGlobalVariable(stack.get(0)));
		}else if(stackSymbol0.getType().equals("size")) {
			int pos=VariableIsAlreadyDeclared(stack.get(0));
			int globalpos=VariableIsGlobal(stack.get(0));

			if(pos!=-1)
				tempStmtLines.add(tratar("aload", pos));
			else if(globalpos!=-1)
				tempStmtLines.add(LoadGlobalVariable(stack.get(0)));
			tempStmtLines.add("arraylength");
		}else if(stackSymbol0.getType().equals("constant")){
			if(Integer.parseInt(stackSymbol0.getName()) <= 5 && Integer.parseInt(stackSymbol0.getName()) >= 0)
				tempStmtLines.add("iconst_"+Integer.parseInt(stackSymbol0.getName()));
			else
				tempStmtLines.add("bipush "+Integer.parseInt(stackSymbol0.getName()));

		}else if(stackSymbol0.getType().equals("arraySize")) {
			int pos=VariableIsAlreadyDeclared(stack.get(0));
			int globalpos=VariableIsGlobal(stack.get(0));

			if(pos!=-1)
				tempStmtLines.add(tratar("iload", pos));
			else if(globalpos!=-1)
				tempStmtLines.add(LoadGlobalVariable(stack.get(0)));
			stackSymbol0=stack.get(1);
			tempStmtLines.add("newarray int");
			pos=VariableIsAlreadyDeclared(stack.get(1));
			globalpos=VariableIsGlobal(stack.get(1));

			if(pos!=-1) {
				tempStmtLines.add(tratar("astore", pos));
			}else if(globalpos!=-1) {
				tempStmtLines.add(StoreGlobalVariable(stack.get(1)));
			}else {
				tempStmtLines.add(tratar("astore",this.localVars.size()));
				this.localVars.add(new Symbol(stackSymbol0.getName(), 1, stackSymbol0.getType()));
			}
		}else if(stackSymbol0.getType().equals("arraySizeC")) {
			if(Integer.parseInt(stackSymbol0.getName()) <= 5 && Integer.parseInt(stackSymbol0.getName()) >= 0)
				tempStmtLines.add("iconst_"+Integer.parseInt(stackSymbol0.getName()));
			else
				tempStmtLines.add("bipush "+Integer.parseInt(stackSymbol0.getName()));
			stackSymbol0=stack.get(1);
			tempStmtLines.add("newarray int");
			int pos=VariableIsAlreadyDeclared(stack.get(1));
			int globalpos=VariableIsGlobal(stack.get(1));

			if(pos!=-1) {
				tempStmtLines.add(tratar("astore", pos));
			}else if(globalpos!=-1) {
				tempStmtLines.add(StoreGlobalVariable(stack.get(1)));
			}else {
				tempStmtLines.add(tratar("astore",this.localVars.size()));
				this.localVars.add(new Symbol(stackSymbol0.getName(), 1, stackSymbol0.getType()));
			}
		}else if(stack.get(0).getType().equals("arrayAccess")){
			int pos=VariableIsAlreadyDeclared(stack.get(0));
			int globalpos=VariableIsGlobal(stack.get(0));
			if(pos!=-1)
				tempStmtLines.add(tratar("aload", pos));
			else if(globalpos!=-1)
				tempStmtLines.add(LoadGlobalVariable(stack.get(0)));
			if(!(stack.get(1).getType().equals("arrayAccess")) && (stack.get(3).getType().equals("condicOp") || stack.get(4).getType().equals("condicOp") )) {
				if(stack.get(1).getType().equals("variable")) {
					pos=VariableIsAlreadyDeclared(stack.get(1));
					globalpos=VariableIsGlobal(stack.get(1));

					if(pos!=-1)
						tempStmtLines.add(tratar("iload", pos));
					else if(globalpos!=-1)
						tempStmtLines.add(LoadGlobalVariable(stack.get(1)));
					tempStmtLines.add("iaload");
				}else if(stackSymbol0.getType().equals("size")) {
					pos=VariableIsAlreadyDeclared(stack.get(0));
					globalpos=VariableIsGlobal(stack.get(0));

					if(pos!=-1)
						tempStmtLines.add(tratar("aload", pos));
					else if(globalpos!=-1)
						tempStmtLines.add(LoadGlobalVariable(stack.get(0)));

					tempStmtLines.add("arraylength");
				}else if(stack.get(1).getType().equals("constant")){
					if(Integer.parseInt(stack.get(1).getName()) <= 5 && Integer.parseInt(stack.get(1).getName()) >= 0)
						tempStmtLines.add("iconst_"+Integer.parseInt(stack.get(1).getName()));
					else
						tempStmtLines.add("bipush "+Integer.parseInt(stack.get(1).getName()));

				}
				stack.remove(1);
			}
		}else{
			System.out.println("Something went Wrong!!!!!1");
			System.out.println(stackSymbol0.getName());
			stmtLines.addAll(stmtLines.size(), tempStmtLines);
			return stmtLines;
		}

		stack.remove(0);
		stackSymbol0=stack.get(0);

		if(stackSymbol0.getType().equals("variable")) {
			int pos=VariableIsAlreadyDeclared(stack.get(0));
			int globalpos=VariableIsGlobal(stack.get(0));

			if(pos!=-1)
				stmtLines.add(tratar("iload",pos));
			else if(globalpos!=-1)
				stmtLines.add(LoadGlobalVariable(stack.get(0)));
		}else if(stackSymbol0.getType().equals("size")) {
			int pos=VariableIsAlreadyDeclared(stack.get(0));
			int globalpos=VariableIsGlobal(stack.get(0));

			if(pos!=-1)
				tempStmtLines.add(tratar("aload",pos));
			else if(globalpos!=-1)
				stmtLines.add(LoadGlobalVariable(stack.get(0)));
			tempStmtLines.add("arraylength");
		}else if(stackSymbol0.getType().equals("constant")){
			if(Integer.parseInt(stackSymbol0.getName()) <= 5 && Integer.parseInt(stackSymbol0.getName()) >= 0)
				stmtLines.add("iconst_"+Integer.parseInt(stackSymbol0.getName()));
			else
				stmtLines.add("bipush "+Integer.parseInt(stackSymbol0.getName()));

		}else if(stackSymbol0.getType().equals("arraySize")) {
			int pos=VariableIsAlreadyDeclared(stack.get(0));
			int globalpos=VariableIsGlobal(stack.get(0));

			if(pos!=-1)
				stmtLines.add(tratar("iload",pos));
			else if(globalpos!=-1)
				stmtLines.add(LoadGlobalVariable(stack.get(0)));
			stackSymbol0=stack.get(1);
			stmtLines.add("newarray int");
			pos=VariableIsAlreadyDeclared(stack.get(1));
			globalpos=VariableIsGlobal(stack.get(1));

			if(pos!=-1) {
				stmtLines.add(tratar("astore",pos));
			}else if(globalpos!=-1) {
				stmtLines.add(StoreGlobalVariable(stack.get(1)));
			}else {
				stmtLines.add(tratar("astore",this.localVars.size()));
				this.localVars.add(new Symbol(stackSymbol0.getName(), 1, stackSymbol0.getType()));
			}
		}else if(stackSymbol0.getType().equals("arraySizeC")) {
			if(Integer.parseInt(stackSymbol0.getName()) <= 5 && Integer.parseInt(stackSymbol0.getName()) >= 0)
				stmtLines.add("iconst_"+Integer.parseInt(stackSymbol0.getName()));
			else
				stmtLines.add("bipush "+Integer.parseInt(stackSymbol0.getName()));
			stackSymbol0=stack.get(1);
			stmtLines.add("newarray int");
			int pos=VariableIsAlreadyDeclared(stack.get(1));
			int globalpos=VariableIsGlobal(stack.get(1));

			if(pos!=-1) {
				stmtLines.add(tratar("astore",pos));
			}else if(globalpos!=-1) {
				stmtLines.add(StoreGlobalVariable(stack.get(1)));
			}else {
				stmtLines.add(tratar("astore",this.localVars.size()));
				this.localVars.add(new Symbol(stackSymbol0.getName(), 1, stackSymbol0.getType()));
			}
		}else if(stack.get(0).getType().equals("arrayAccess")){
			int pos=VariableIsAlreadyDeclared(stack.get(0));
			int globalpos=VariableIsGlobal(stack.get(0));
			if(pos!=-1) {
				stmtLines.add(tratar("aload",pos));
			}else if(globalpos!=-1) {
				stmtLines.add(LoadGlobalVariable(stack.get(0)));
			}
			if(!stack.get(1).getType().equals("condicOp")) {
				if(stack.get(1).getType().equals("variable")) {
					pos=VariableIsAlreadyDeclared(stack.get(1));
					globalpos=VariableIsGlobal(stack.get(1));
					if(pos!=-1)
						stmtLines.add(tratar("iload",pos));
					else if(globalpos!=-1)
						stmtLines.add(LoadGlobalVariable(stack.get(1)));
					stmtLines.add("iaload");
				}else if(stackSymbol0.getType().equals("size")) {
					pos=VariableIsAlreadyDeclared(stack.get(0));
					globalpos=VariableIsGlobal(stack.get(0));

					if(pos!=-1)
						tempStmtLines.add(tratar("aload",pos));
					else if(globalpos!=-1)
						stmtLines.add(LoadGlobalVariable(stack.get(0)));
						tempStmtLines.add("arraylength");
				}else if(stack.get(1).getType().equals("constant")){
					if(Integer.parseInt(stack.get(1).getName()) <= 5 && Integer.parseInt(stack.get(1).getName()) >= 0)
						stmtLines.add("iconst_"+Integer.parseInt(stack.get(1).getName()));
					else
						stmtLines.add("bipush "+Integer.parseInt(stack.get(1).getName()));
				}
				stack.remove(1);
			}
		}else{
			System.out.println(stackSymbol0.getName());
			System.out.println(stackSymbol0.getType());
			System.out.println(stack.get(0).getName());
			System.out.println(stack.get(0).getType());
			System.out.println("Something went Wrog!!!!!3   :(");
			return stmtLines;
		}

		stack.remove(0);
		stackSymbol0=stack.get(0);
		stmtLines.addAll(stmtLines.size(), tempStmtLines);

		if(stackSymbol0.getType().equals("condicOp") && referenceFlag) {
			switch(stackSymbol0.getName()) {
			case "==":
				stmtLines.add("if_acmpne loop"+this.NoJumps+"_end");
				break;
			case "!=":
				stmtLines.add("if_acmpeq loop"+this.NoJumps+"_end");
				break;
			default:
				System.out.println(stackSymbol0.getName());
				System.out.println(stackSymbol0.getType());
				System.out.println("Condition Operation not reconignized");
				return stmtLines;
			}
		}

		if(stackSymbol0.getType().equals("condicOp")) {
			switch(stackSymbol0.getName()) {
			case "==":
				stmtLines.add("if_icmpne loop"+this.NoJumps+"_end");
				break;
			case "!=":
				stmtLines.add("if_icmpeq loop"+this.NoJumps+"_end");
				break;
			case "<":
				stmtLines.add("if_icmpge loop"+this.NoJumps+"_end");
				break;
			case ">":
				stmtLines.add("if_icmple loop"+this.NoJumps+"_end");
				break;
			case "<=":
				stmtLines.add("if_icmpgt loop"+this.NoJumps+"_end");
				break;
			case ">=":
				stmtLines.add("if_icmplt loop"+this.NoJumps+"_end");
				break;
			default:
				System.out.println(stackSymbol0.getName());
				System.out.println(stackSymbol0.getType());
				System.out.println("Condition Operation not reconignized");
				return stmtLines;
			}
			stmtLines.add("");
		}else {
			System.out.println(stackSymbol0.getName());
			System.out.println(stackSymbol0.getType());
			System.out.println("Something went Wrog!!!!!3   :( X(");
			return stmtLines;
		}

		stack.remove(0);
		return stmtLines;
	}

	public int findNextAssign() {
		for(int i=0; i<stack.size(); i++) {
			if(stack.get(i).getName().equals("=")) {
				return i;
			}
		}
		return -1;
	}

	public Vector<String> Build_Aritmetic_Lines(){
		Vector<String> stmtLines = new Vector<String>();
		Vector<String> tempStmtLines = new Vector<String>();
		boolean astoreFlag=false;
		Symbol stackSymbol0=stack.get(0);

		while(!stackSymbol0.getName().equals("=")){

			if(stack.size()>1){
				if(stack.get(1).getName().equals("=") && !astoreFlag){
					int pos=VariableIsAlreadyDeclared(stack.get(0));
					int globalpos=VariableIsGlobal(stack.get(0));

					if(pos!=-1)
						stmtLines.add(tratar("istore", pos));
					else if(globalpos!=-1) {
						stmtLines.add(StoreGlobalVariable(stack.get(0)));
					}else{
						stmtLines.add(tratar("istore", this.localVars.size()));
						this.localVars.add(new Symbol(stack.get(0).getName(), 0, "variable"));
					}
					stack.remove(0);
					if(stack.size()>0) {
						stackSymbol0=stack.get(0);
						continue;
					}else {
						break;
					}
				}
			}

			int nextAssign=findNextAssign();
			if(nextAssign!=-1 && nextAssign!=0 && nextAssign>2) {
				if(stack.get(nextAssign-2).getType().equals("arrayAccess")) {
					astoreFlag=true;
					int pos=VariableIsAlreadyDeclared(stack.get(nextAssign-2));
					int globalpos=VariableIsGlobal(stack.get(nextAssign-2));

					if(pos!=-1) {
						stmtLines.add(tratar("aload",pos));
					}else if(globalpos!=-1) {
						stmtLines.add(LoadGlobalVariable(stack.get(nextAssign-2)));
					}
					if(stack.get(nextAssign-1).getType().equals("variable")) {
						pos=VariableIsAlreadyDeclared(stack.get(nextAssign-1));
						globalpos=VariableIsGlobal(stack.get(nextAssign-1));
						if(pos!=-1)
							stmtLines.add(tratar("iload",pos));
						else if(globalpos!=-1)
							stmtLines.add(LoadGlobalVariable(stack.get(nextAssign-1)));
					}else if(stack.get(nextAssign-1).getType().equals("constant")) {
						if(Integer.parseInt(stack.get(nextAssign-1).getName()) <= 5 && Integer.parseInt(stack.get(nextAssign-1).getName()) >= 0)
							stmtLines.add("iconst_"+Integer.parseInt(stack.get(nextAssign-1).getName()));
						else
							stmtLines.add("bipush "+Integer.parseInt(stack.get(nextAssign-1).getName()));
					}

					pos=VariableIsAlreadyDeclared(stack.get(nextAssign-2));
					globalpos=VariableIsGlobal(stack.get(nextAssign-2));

					if(pos!=-1) {
						tempStmtLines.add("iastore");
					}else if(globalpos!=-1) {
						stmtLines.add(StoreGlobalVariable(stack.get(nextAssign-2)));
					}else {
						System.out.println("Error: This is not possible in the language, I think");
						tempStmtLines.add("newarray int");
						tempStmtLines.add("iastore");
						this.localVars.add(new Symbol(stack.get(nextAssign-2).getName(), 1, "variable"));
					}
					stack.remove(nextAssign-1);
					stack.remove(nextAssign-2);

					if(stack.size()>0) {
						stackSymbol0=stack.get(0);
						continue;
					}else {
						break;
					}
				}
			}


			if(stack.size()>2) {
				if(stack.get(2).getName().equals("=") && !VariableIsScalar(stack.get(1))){

					Symbol arrayValue = stack.get(0);
					Symbol arrayId =stack.get(1);
					stack.remove(0);
					stack.remove(0);
					stack.remove(0);

					System.out.println("Old Stack");
					for(int i = 0; i < 3; i++) {
						System.out.println(stack.get(i).getType() + ": " + stack.get(i).getName());
					}

					Symbol temp= new Symbol("end_while", 0, "end_while");
					stack.add(0,temp);

					temp= new Symbol("=", 0, "assign");
					stack.add(0,temp);
					temp= new Symbol("@t"+this.NoTempVariables, 0, "variable");
					stack.add(0,temp);
					temp= new Symbol("+", 0, "operator");
					stack.add(0,temp);
					temp= new Symbol("@t"+this.NoTempVariables, 0, "variable");
					stack.add(0,temp);
					temp= new Symbol("1", 0, "constant");
					stack.add(0,temp);


					temp= new Symbol("=", 0, "assign");
					stack.add(0,temp);
					temp= new Symbol("@t"+this.NoTempVariables, 0, "variable");
					stack.add(0,temp);
					temp = new Symbol(arrayId.getName(), 1, "arrayAccess");
					stack.add(0, temp);
					stack.add(0, arrayValue);
					temp= new Symbol("<", 0, "condicOp");
					stack.add(0,temp);
					temp= new Symbol("@t"+this.NoTempVariables, 0, "variable");
					stack.add(0,temp);
					temp= new Symbol("a", 1, "size");
					stack.add(0,temp);
					temp= new Symbol("while", 0, "while");
					stack.add(0,temp);
					temp= new Symbol("=", 0, "assign");
					stack.add(0,temp);
					temp= new Symbol("@t"+this.NoTempVariables, 0, "variable");
					stack.add(0,temp);
					Symbol temp1= new Symbol("+0", 0, "constant");
					stack.add(0, temp1);

					System.out.println("New Stack");
					for(int i = 0; i < 12; i++) {
						System.out.println(stack.get(i).getType() + ": " + stack.get(i).getName());
					}
					stackSymbol0 = stack.get(0);
					this.NoTempVariables++;
					continue;
				}
			}

			if(stackSymbol0.getType().equals("variable")){
				int pos=VariableIsAlreadyDeclared(stack.get(0));
				int globalpos=VariableIsGlobal(stack.get(0));

				if(pos!=-1)
					stmtLines.add(tratar("iload",pos));
				else if(globalpos!=-1)
					stmtLines.add(LoadGlobalVariable(stack.get(0)));
				stack.remove(0);
				if(stack.size()>0) {
					stackSymbol0=stack.get(0);
					continue;
				}else {
					break;
				}
			}

			if(stackSymbol0.getType().equals("constant")){
				System.out.println("constant: " + stackSymbol0.getName());
				System.out.println("constant: " + stack.get(1).getName());
				System.out.println("constant: " + stack.get(2).getName());
				if(Integer.parseInt(stackSymbol0.getName()) <= 5 && Integer.parseInt(stackSymbol0.getName()) >= 0)
					stmtLines.add("iconst_"+Integer.parseInt(stackSymbol0.getName()));
				else
					stmtLines.add("bipush "+Integer.parseInt(stackSymbol0.getName()));
				stack.remove(0);
				if(stack.size()>0) {
					stackSymbol0=stack.get(0);
					continue;
				}else {
					break;
				}
			}

			if(stackSymbol0.getType().equals("arraySize")){
				int pos=VariableIsAlreadyDeclared(stack.get(0));
				int globalpos=VariableIsGlobal(stack.get(0));
				if(pos!=-1)
					stmtLines.add(tratar("iload",pos));
				else if(globalpos!=-1)
					stmtLines.add(LoadGlobalVariable(stack.get(0)));
				stack.remove(0);
				stackSymbol0=stack.get(0);
				stmtLines.add("newarray int");
				pos=VariableIsAlreadyDeclared(stackSymbol0);
				globalpos=VariableIsGlobal(stackSymbol0);

				if(pos!=-1) {
					stmtLines.add(tratar("astore",pos));
				}else if(globalpos!=-1){
					stmtLines.add(StoreGlobalVariable(stackSymbol0));
				}else {
					stmtLines.add(tratar("astore",this.localVars.size()));
					this.localVars.add(new Symbol(stackSymbol0.getName(), 1, "variable"));
				}
				stack.remove(0);
				if(stack.size()>0) {
					stackSymbol0=stack.get(0);
					continue;
				}else {
					break;
				}
			}
			if(stackSymbol0.getType().equals("arraySizeA")){
				int pos=VariableIsAlreadyDeclared(stack.get(0));
				int globalpos=VariableIsGlobal(stack.get(0));
				if(pos!=-1)
					stmtLines.add(tratar("aload",pos));
				else if(globalpos!=-1)
					stmtLines.add(LoadGlobalVariable(stack.get(0)));
				stmtLines.add("arraylength");
				stack.remove(0);
				stackSymbol0=stack.get(0);
				stmtLines.add("newarray int");
				pos=VariableIsAlreadyDeclared(stackSymbol0);
				globalpos=VariableIsGlobal(stackSymbol0);

				if(pos!=-1) {
					stmtLines.add(tratar("astore",pos));
				}else if(globalpos!=-1){
					stmtLines.add(StoreGlobalVariable(stackSymbol0));
				}else {
					stmtLines.add(tratar("astore",this.localVars.size()));
					this.localVars.add(new Symbol(stackSymbol0.getName(), 1, "variable"));
				}
				stack.remove(0);
				if(stack.size()>0) {
					stackSymbol0=stack.get(0);
					continue;
				}else {
					break;
				}
			}

			if(stackSymbol0.getType().equals("arraySizeC")){
				if(Integer.parseInt(stackSymbol0.getName()) <= 5 && Integer.parseInt(stackSymbol0.getName()) >= 0)
					stmtLines.add("iconst_"+Integer.parseInt(stackSymbol0.getName()));
				else
					stmtLines.add("bipush "+Integer.parseInt(stackSymbol0.getName()));
				stack.remove(0);
				stackSymbol0=stack.get(0);
				stmtLines.add("newarray int");
				int pos=VariableIsAlreadyDeclared(stack.get(0));
				int globalpos=VariableIsGlobal(stack.get(0));

				if(pos!=-1) {
					stmtLines.add(tratar("astore",pos));
				}else if(globalpos!=-1){
					stmtLines.add(StoreGlobalVariable(stack.get(0)));
				}else {
					stmtLines.add(tratar("astore",this.localVars.size()));
					this.localVars.add(new Symbol(stackSymbol0.getName(), 1, "variable"));
				}
				stack.remove(0);
				if(stack.size()>0) {
					stackSymbol0=stack.get(0);
					continue;
				}else {
					break;
				}
			}

			if(stack.get(0).getType().equals("arrayAccess")){
				int pos=VariableIsAlreadyDeclared(stack.get(0));
				int globalpos=VariableIsGlobal(stack.get(0));
				if(pos!=-1)
					stmtLines.add(tratar("aload",pos));
				else if(globalpos!=-1)
					stmtLines.add(LoadGlobalVariable(stack.get(0)));
				stack.remove(0);

				if(stack.get(0).getType().equals("variable")) {
					pos=VariableIsAlreadyDeclared(stack.get(0));
					globalpos=VariableIsGlobal(stack.get(0));
					if(pos!=-1)
						stmtLines.add(tratar("iload",pos));
					else if(globalpos!=-1)
						stmtLines.add(LoadGlobalVariable(stack.get(0)));
				}else if(stack.get(0).getType().equals("constant")) {
					if(Integer.parseInt(stack.get(0).getName()) <= 5 && Integer.parseInt(stack.get(0).getName()) >= 0)
						stmtLines.add("iconst_"+Integer.parseInt(stack.get(0).getName()));
					else
						stmtLines.add("bipush "+Integer.parseInt(stack.get(0).getName()));
				}
				stack.remove(0);

				stmtLines.add("iaload");

				if(stack.size()>0) {
					stackSymbol0=stack.get(0);
					continue;
				}else {
					break;
				}
			}

			if(stack.get(0).getType().equals("operator")) {
				if(stack.get(0).getName().equals("*")) {
					stmtLines.add("imul");
					stack.remove(0);
				}	else if(stack.get(0).getName().equals("/")) {
					stmtLines.add("idiv");
					stack.remove(0);
				}	else if(stack.get(0).getName().equals("+")) {
					stmtLines.add("iadd");
					stack.remove(0);
				}	else if(stack.get(0).getName().equals("-")) {
					stmtLines.add("isub");
					stack.remove(0);
				}	else if(stack.get(0).getName().equals("&")) {
					stmtLines.add("iand");
					stack.remove(0);
				}	else if(stack.get(0).getName().equals("<<")) {
					stmtLines.add("ishl");
					stack.remove(0);
				}	else if(stack.get(0).getName().equals(">>")) {
					stmtLines.add("ishr");
					stack.remove(0);
				}	else if(stack.get(0).getName().equals("|")) {
					stmtLines.add("ior");
					stack.remove(0);
				} else{
					System.out.println("Something went Wrog!!!!!1");
					System.out.println(stackSymbol0.getName());
					break;
				}

				if(stack.size()>0) {
					stackSymbol0=stack.get(0);
					continue;
				}else {
					break;
				}
			}


			if(stackSymbol0.getType().equals("funcInvokeArg")){
				Vector<Symbol> callArgs = new Vector<Symbol>();
				while(stackSymbol0.getType().equals("funcInvokeArg")) {
					callArgs.add(stackSymbol0);
					stack.remove(0);
					stackSymbol0=stack.get(0);
				}

				stackSymbol0=stack.get(0);
				stack.remove(0);
				String funcName="";
				if(stackSymbol0.getType().equals("callFunc")) {
					funcName = stackSymbol0.getName();
				}else {
					System.out.println("Something went Wrong!!!");
				}

				for(int i=0; i<callArgs.size(); i++) {
					if(!isInteger(callArgs.get(i).getName())) {
						int pos=VariableIsAlreadyDeclared(callArgs.get(i));
						int globalpos=VariableIsGlobal(callArgs.get(i));

						if(pos != -1) {
							if(VariableIsScalar(callArgs.get(i)))
								stmtLines.add(tratar("iload",pos));
							else
								stmtLines.add(tratar("aload",pos));
						}
						else if(globalpos != -1) {
							stmtLines.add(LoadGlobalVariable(callArgs.get(i)));
						}
					}else if(isInteger(callArgs.get(i).getName())){
							if(Integer.parseInt(callArgs.get(i).getName()) <= 5 && Integer.parseInt(callArgs.get(i).getName()) >= 0)
								stmtLines.add("iconst_"+Integer.parseInt(callArgs.get(i).getName()));
							else
								stmtLines.add("bipush "+Integer.parseInt(callArgs.get(i).getName()));
					}else{
						System.out.println("Something went Wrog!!!!!3    x(");
						return stmtLines;
					}
				}

				String tempLine;
				if(funcName.contains(".")){
					String[] split = funcName.split("\\.");
					tempLine="invokestatic " + split[0] + "/" + split[1] + "(";
					for(int i=0; i<callArgs.size(); i++){
						int pos=VariableIsAlreadyDeclared(callArgs.get(i));
						int globalpos=VariableIsGlobal(callArgs.get(i));

						if(callArgs.get(i).getName().contains("\"")) {
							tempLine+="Ljava/lang/String;";
						}else if(pos!=-1) {
							if(VariableIsScalar(callArgs.get(i)))
								stmtLines.add(tratar("iload",pos));
							else
								stmtLines.add(tratar("aload",pos));
						}else if(globalpos!=-1) {
							stmtLines.add(LoadGlobalVariable(callArgs.get(i)));
						}
					}
					tempLine+=")";
					if(stack.size()>0 && stack.get(2).getName().equals("=")) {
						if(VariableIsScalar(stack.get(0))){
							tempLine+="I";
						}else{
							tempLine+="[I";
						}

					}else {
						tempLine+="V";
					}
				}else {
					tempLine="invokestatic "+module+"/"+funcName+"(";

					for(int i=0; i<callArgs.size(); i++){
						if(callArgs.get(i).getName().contains("\"")) {
							tempLine+="Ljava/lang/String;";
						}else if(!VariableIsScalar(callArgs.get(i))){
							tempLine+="[I";
						}else{
							tempLine+="I";
						}
					}
					tempLine+=")";
					System.out.println("funcName2: " + funcName);
					if(stack.size()>0 && stack.get(1).getName().equals("=")) {
						if(getFunctionReturn(funcName) == 0){
							tempLine+="I";
						}else{
							tempLine+="[I";
						}


					}else {
						tempLine+="V";
					}
				}

				stmtLines.add(tempLine);
				System.out.println("Build_Aritmetic_Lines()1>>>>>Size: " + stack.size());
				if(stack.size() > 2 && stack.get(1).getName().equals("=")) {
					if(stack.get(0).getType().equals("variable")) {
						int pos=VariableIsAlreadyDeclared(stack.get(0));
						int globalpos=VariableIsGlobal(stack.get(0));

						if(pos!=-1)
							stmtLines.add(tratar("istore", pos));
						else if(globalpos!=-1){
							stmtLines.add(StoreGlobalVariable(stack.get(0)));
						}
						else {
							stmtLines.add(tratar("istore", this.localVars.size()));
							this.localVars.add(stack.get(0));
						}
						stack.remove(0);
						stack.remove(0);
					}
					else if(stack.size() > 1 && stack.get(0).getType().equals("arrayAccess")) {
						int pos=VariableIsAlreadyDeclared(stack.get(0));
						int globalpos=VariableIsGlobal(stack.get(0));

						if(pos!=-1)
							stmtLines.add(tratar("aload",pos));
						else if(globalpos!=-1)
							stmtLines.add(LoadGlobalVariable(stack.get(0)));
						stack.remove(0);
						pos=VariableIsAlreadyDeclared(stack.get(0));
						globalpos=VariableIsGlobal(stack.get(0));
						if(pos!=-1)
							stmtLines.add(tratar("iload",pos));
						else if(globalpos!=-1)
							stmtLines.add(LoadGlobalVariable(stack.get(0)));
						stack.remove(0);
						stmtLines.add("iastore");
					}
				}

				if(stack.size()>0) {
					stackSymbol0=stack.get(0);
					continue;
				}else {
					break;
				}
			}

			if(stackSymbol0.getType().equals("size")) {
				int pos=VariableIsAlreadyDeclared(stack.get(0));
				int globalpos=VariableIsGlobal(stack.get(0));
				if(pos!=-1)
					stmtLines.add(tratar("aload",pos));
				else if(globalpos!=-1)
					stmtLines.add(LoadGlobalVariable(stack.get(0)));
				stack.remove(0);
				stmtLines.add("arraylength");

				if(stack.size()>0) {
					stackSymbol0=stack.get(0);
					continue;
				}else {
					break;
				}
			}

			if(stackSymbol0.getType().equals("callFunc")){
				String funcName = stackSymbol0.getName();
				Vector<Symbol> callArgs = new Vector<Symbol>();

				String tempLine;
				if(funcName.contains(".")){
					String[] split = funcName.split("\\.");
					tempLine="invokestatic " + split[0] + "/" + split[1] + "(";
					for(int i=0; i<callArgs.size(); i++){
						if(callArgs.get(i).getName().contains("\"")) {
							tempLine+="Ljava/lang/String;";
						}else if(!VariableIsScalar(callArgs.get(i))){
							tempLine+="[I";
						}else{
							tempLine+="I";
						}
					}
					tempLine+=")";
					if(stack.size()>0 && stack.get(2).getName().equals("=")) {
						if(VariableIsScalar(stack.get(0))){
							tempLine+="I";
						}else{
							tempLine+="[I";
						}

					}else {
						tempLine+="V";
					}
				}else {
					tempLine="invokestatic "+module+"/"+funcName+"(";

					for(int i=0; i<callArgs.size(); i++){
						if(callArgs.get(i).getName().contains("\"")) {
							tempLine+="Ljava/lang/String;";
						}else if(callArgs.get(i).getScalarOrArray()==0){
							tempLine+="I";
						}else{
							tempLine+="[I";
						}
					}
					tempLine+=")";
					if(stack.size()>0 && stack.get(2).getName().equals("=")) {
						if(getFunctionReturn(funcName) == 0){
							tempLine+="I";
						}else{
							tempLine+="[I";
						}
					}else {
						tempLine+="V";
					}
				}

				stmtLines.add(tempLine);
				System.out.println("Build_Aritmetic_Lines()2>>>>>>>>>>>Size: " + stack.size());
				stack.remove(0);

				if(stack.size()>0) {
					stackSymbol0=stack.get(0);
					continue;
				}else {
					break;
				}
			}



		}
		stack.remove(0);
		stmtLines.addAll(stmtLines.size(), tempStmtLines);
		return stmtLines;
	}

	public String tratar (String line, int pos){
		if(pos<=3){
			return line + "_" + pos;
		}
		return line + " " + pos;
	}

	public void changeStackToLowerCase() {
		for(int i=0; i<stack.size(); i++) {
			stack.get(i).changeNameToLowerCase();
		}
	}

	public int getStackLimit(Vector<String> stmtLines) {
		int stackSize=0, maxStackSize=0;
		for(int i=0; i<stmtLines.size(); i++) {

			if (stmtLines.get(i).contains("astore") || stmtLines.get(i).contains("if_") || stmtLines.get(i).contains("iadd") || stmtLines.get(i).contains("isub") || stmtLines.get(i).contains("imul") || stmtLines.get(i).contains("idiv") || stmtLines.get(i).contains("iand") || stmtLines.get(i).contains("ior") || stmtLines.get(i).contains("ishl") || stmtLines.get(i).contains("ishr"))
				stackSize -= 2;
			else if (stmtLines.get(i).contains("istore") || stmtLines.get(i).contains("iaload") || stmtLines.get(i).contains("ireturn") || stmtLines.get(i).contains("areturn"))
				stackSize -= 1;
			else if (stmtLines.get(i).contains("iastore"))
				stackSize -= 3;
			else if (stmtLines.get(i).contains("invokestatic")) {
				String[] final_string = stmtLines.get(i).split("\\(");
				String[] args = final_string[1].split("\\)");
				int argNumber = 0;
				int lastIndex = 0;
				String findStr = "I";
				while(lastIndex != -1){

				    lastIndex = args[0].indexOf(findStr,lastIndex);

				    if(lastIndex != -1){
				        argNumber ++;
				        lastIndex += findStr.length();
				    }
				}
				findStr = "Ljava/lang/String;";
				lastIndex = 0;
				while(lastIndex != -1){

				    lastIndex = args[0].indexOf(findStr,lastIndex);

				    if(lastIndex != -1){
				        argNumber ++;
				        lastIndex += findStr.length();
				    }
				}
				stackSize -= argNumber;
				if(!args[1].contains("V")) {
					stackSize++;
				}
			}
			else if(stmtLines.get(i).equals("") || stmtLines.get(i).contains("arraylength") || stmtLines.get(i).contains("iinc") || stmtLines.get(i).contains("loop") || stmtLines.get(i).contains(".method") || stmtLines.get(i).contains(".end") || stmtLines.get(i).contains(".limit") || stmtLines.get(i).contains(".super") || stmtLines.get(i).contains(".class"))
				continue;
			else {
				stackSize++;
			}
			System.out.println("stackSize: " + stackSize + " at: " + stmtLines.get(i));
			if(stackSize>maxStackSize) {
				maxStackSize=stackSize;
			}
		}
		return maxStackSize;
	}

}
