import java.util.*;

public class PostorderVisitor extends YAL2JVMDefaultVisitor {

  private int i = 0;
  private int j;

  public void reverseStack(){
    Collections.reverse(stack);
  }

  public Vector<Symbol> getStack(){
      return stack;
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

    private Vector<Symbol> stack = new Vector<Symbol>();

    private Vector<String> arthExp = new Vector<String>();

/*
    public void makeExpression(){
        String operator = stack.pop();
        String opr2 = stack.pop();
        String opr1 = stack.pop();
        String save = stack.pop();

        // Este assume que os dois operandos são variáveis
        if(!isInteger(opr1) && !isInteger(opr2)){
          arthExp.add("_t"+i+"="+opr1+operator+opr2+";");
          arthExp.add(save+"="+"_t"+i+";");
        }
        //Este assume que o operando da esquerda é uma constante e o da direita é variável
        else if(isInteger(opr1) && !isInteger(opr2)){
          arthExp.add("_t"+i+"="+opr1+";");
          j=i;
          i++;
          arthExp.add("_t"+i+"="+"_t"+j+operator+opr2+";");
          arthExp.add(save+"="+"_t"+i);
        }
        //último caso ao contrário
        else if(!isInteger(opr1) && isInteger(opr2)){
          arthExp.add("_t"+i+"="+opr2+";");
          j=i;
          i++;
          arthExp.add("_t"+i+"="+opr1+operator+"_t"+j+";");
          arthExp.add(save+"="+"_t"+i);
        }
*/

/*
        i++;

        System.out.println("-------");
        for(int k=0; k< arthExp.size(); k++){
          System.out.println(arthExp.get(k));
        }
        System.out.println("-------");


    }*/

  public Object visit(ASTModule node, Object data){
    Object obj = new Object();
    for(int i = node.jjtGetNumChildren()-1; i >=0 ; i--){
      if(node.jjtGetChild(i).getId() == YAL2JVMTreeConstants.JJTFUNCTION)
        obj=visit((ASTFunction)(node.jjtGetChild(i)), data);
      if(node.jjtGetChild(i).getId() == YAL2JVMTreeConstants.JJTDECLARATION)
        obj=visit((ASTDeclaration)(node.jjtGetChild(i)), data);
    }
    Symbol sym = new Symbol(node.getName(), 0, "module");
    stack.add(sym);

    return obj;
  }

  public Object visit(ASTDeclaration node, Object data){
    Object obj=super.visit(node, data);
    return obj;
  }

  public Object visit(ASTScalarElement node, Object data){
    Object obj=super.visit(node, data);
    if(node.jjtGetParent().getId() == YAL2JVMTreeConstants.JJTDECLARATION) {
      System.out.println(node.jjtGetNumChildren());
      if(node.jjtGetParent().jjtGetNumChildren() > 1 && node.jjtGetParent().jjtGetChild(1).getId() == YAL2JVMTreeConstants.JJTARRAYSIZE) {
        Symbol sym = new Symbol(node.getName(), 1, "global");
        stack.add(sym);
      } else {
        Symbol sym = new Symbol(node.getName(), 0, "global");
        stack.add(sym);
      }
    }
    return obj;
  }

  public Object visit(ASTArrayElement node, Object data){
    Object obj=super.visit(node, data);
    if(node.jjtGetParent().getId() == YAL2JVMTreeConstants.JJTDECLARATION) {
      Symbol sym = new Symbol(node.getName(), 1, "global");
      stack.add(sym);
    }    return obj;
  }

  public Object visit(ASTStmtlst node, Object data){
    Object obj=new Object();
    for(int i = node.jjtGetNumChildren()-1; i >=0 ; i--){
       obj=super.visit((ASTStmt)(node.jjtGetChild(i)), data);
      }
    return obj;
  }


  //Finds Operator Type
   public Object visit(ASTRhs node, Object data) {
    if(node.getOp()!=null){
      Symbol sym = new Symbol(node.getOp(), 0, "operator");
      stack.add(sym);
      //makeExpression();
      //System.out.println(sym.getType()+": "+sym.getName());
    }
    Object obj=new Object();
    for(int i = node.jjtGetNumChildren()-1; i >=0 ; i--){
        System.out.println(node.jjtGetChild(i).getId());
        if(node.jjtGetChild(i).getId() == YAL2JVMTreeConstants.JJTTERM)
            obj=visit((ASTTerm)(node.jjtGetChild(i)), data);
        if(node.jjtGetChild(i).getId() == YAL2JVMTreeConstants.JJTSCALARACCESS)
          obj=visit((ASTScalarAccess)(node.jjtGetChild(i)), data);
        if(node.jjtGetChild(i).getId() == YAL2JVMTreeConstants.JJTARRAYSIZE)
          obj=visit((ASTArraySize)(node.jjtGetChild(i)), data);
      }
    return obj;
  }

    //Finds Variable Operands
    public Object visit(ASTScalarAccess node, Object data) {
    	if(node.jjtGetParent().getId()!=YAL2JVMTreeConstants.JJTARRAYSIZE) {
        if(node.getSize() == null) {
      		Symbol sym = new Symbol(node.getName(), 0, "variable");
      		stack.add(sym);
        }
        else {
          Symbol sym = new Symbol(node.getName(), 0, "size");
          stack.add(sym);
        }
    	}
    //System.out.println(sym.getType()+": "+sym.getName());
    Object obj = super.visit(node, data);
    return obj;
  }

/*
    //Finds Variable Operands
    public Object visit(ASTArrayAccess node, Object data) {
    stack.push(node.getName());
    System.out.println(node.getName());
    Object obj = super.visit(node, data);
    return obj;
  }

*/

  //Finds Constant Operands
  public Object visit(ASTTerm node, Object data) {
    if(node.getName()!= null){
      Symbol sym = new Symbol(node.getOp()+node.getName(), 0, "constant");
      stack.add(sym);
      //System.out.println(sym.getType()+": "+sym.getName());
    }
    Object obj = super.visit(node, data);
    return obj;
  }

  //Finds Constant Operands
  /*public Object visit(ASTIndex node, Object data) {
    if(node.getName()!= null){
      stack.push(node.getName());
      System.out.println(node.getName());
    }
    Object obj = super.visit(node, data);
    return obj;
  }*/

  public void getVarlist(ASTVarlist node){

    for(int i = 0; i < node.jjtGetNumChildren(); i++){

      Symbol sy = new Symbol("", 0, "");

      if(node.jjtGetChild(0).getId() == YAL2JVMTreeConstants.JJTSCALARELEMENT)
           sy = new Symbol(((ASTScalarElement)node.jjtGetChild(i)).getName(), 0, "funcDeclArg");
      else if(node.jjtGetChild(0).getId() == YAL2JVMTreeConstants.JJTARRAYELEMENT)
           sy = new Symbol(((ASTArrayElement)node.jjtGetChild(i)).getName(), 0, "funcDeclArgArray");
      else
           sy = new Symbol("void", 0, "returnType");

      //System.out.println(sy.getType()+": "+sy.getName());

      stack.add(sy);
    }
  }

  public Object visit(ASTFunction node, Object data) {

    Object obj = super.visit(node, data);

    Symbol sy = new Symbol("", 0, "");

    if(node.jjtGetChild(0).getId() == YAL2JVMTreeConstants.JJTSCALARELEMENT)
         sy = new Symbol(((ASTScalarElement)node.jjtGetChild(0)).getName(), 0, "returnTypeS");
    else if(node.jjtGetChild(0).getId() == YAL2JVMTreeConstants.JJTARRAYELEMENT)
         sy = new Symbol(((ASTArrayElement)node.jjtGetChild(0)).getName(), 0, "returnTypeA");
    else
         sy = new Symbol("void", 0, "returnType");

       //System.out.println(sy.getType()+": "+sy.getName());

    stack.add(sy);


    for(int i = 0; i < node.jjtGetNumChildren(); i++) {
      if(node.jjtGetChild(i).getId() == YAL2JVMTreeConstants.JJTVARLIST) {
        getVarlist((ASTVarlist)node.jjtGetChild(i));
        Symbol sym = new Symbol(Integer.toString(node.jjtGetChild(i).jjtGetNumChildren()), 0, "nrOfArgs");
        stack.add(sym);
        //System.out.println(sym.getType()+": "+sym.getName());
      }
    }

    if(node.getName()!= null){
      Symbol sym = new Symbol(node.getName(), 0, "function");
      stack.add(sym);
      //System.out.println(sym.getType()+": "+sym.getName());
    }

    return obj;
  }

  public Object visit(ASTAssign node, Object data) {

    Symbol sym = new Symbol("=", 0, "assign");
    stack.add(sym);

    //System.out.println(sym.getType()+": "+sym.getName());

    Object obj = super.visit(node, data);
    return obj;

  }

  public Object visit(ASTArgumentList node, Object data) {
    Object obj = super.visit(node, data);

    for(int i = node.jjtGetNumChildren()-1; i>=0; i--){
       Symbol sym = new Symbol(" ", 0, " ");

      if(((ASTArgument)node.jjtGetChild(i)).getName()!=null)
          sym = new Symbol(((ASTArgument)node.jjtGetChild(i)).getName(), 0, "funcInvokeArg");
      else if(((ASTArgument)node.jjtGetChild(i)).getNumber()!=null)
          sym = new Symbol(((ASTArgument)node.jjtGetChild(i)).getNumber(), 0, "funcInvokeArg");
      else if(((ASTArgument)node.jjtGetChild(i)).getString()!=null)
          sym = new Symbol(((ASTArgument)node.jjtGetChild(i)).getString(), 0, "funcInvokeArg");

        //System.out.println(sym.getType()+": "+sym.getName());
      stack.add(sym);
      }

    return obj;
  }


// public Object visit(ASTArgument node, Object data) {

//       Object obj = super.visit(node, data);

//       Symbol sym = new Symbol(" ", 0, " ");

//       if(node.getName()!=null)
//           sym = new Symbol(node.getName(), 0, "variable");
//       else if(node.getNumber()!=null)
//           sym = new Symbol(node.getNumber(), 0, "funcInvokeArg");
//       else if(node.getString()!=null)
//           sym = new Symbol(node.getString(), 0, "funcInvokeArg");

//         //System.out.println(sym.getType()+": "+sym.getName());


//       stack.add(sym);

//       return obj;

//   }

  public Object visit(ASTCall node, Object data) {
    //Symbol sy = new Symbol(Integer.toString(node.jjtGetChild(0).jjtGetNumChildren()), 0, "nrOfArgssss");
    //stack.add(sy);

    Symbol sym;
    if(node.getId2()==null || node.getId2().equals("")){
      sym = new Symbol(node.getId1(), 0, "callFunc");
    }
    else{
      sym = new Symbol(node.getId1()+"."+node.getId2(),0,"callFunc");
    }
    stack.add(sym);

    Object obj = super.visit(node, data);

    return obj;

  }


  public Object visit(ASTCallStmt node, Object data) {
    Symbol sym;

    if(node.getId2().equals("")){
      sym = new Symbol(node.getId1(), 0, "callFunc");
    }
    else{
      sym = new Symbol(node.getId1()+"."+node.getId2(),0,"callFunc");
    }
    stack.add(sym);
    //System.out.println(sym.getType()+": "+sym.getName());

    Object obj = super.visit(node, data);
    return obj;

  }


  public Object visit(ASTArraySize node, Object data) {
    Symbol sym;

    if(node.getName() != null)
      sym = new Symbol(node.getName(), 0, "arraySizeC");
    else {
      ASTScalarAccess child = ((ASTScalarAccess)node.jjtGetChild(0));
      if(child.getSize() == null)
        sym = new Symbol(child.getName(), 0, "arraySize");
      else
        sym = new Symbol(child.getName(), 0, "arraySizeA");
    }
    stack.add(sym);

    Object obj = super.visit(node, data);

    return obj;

  }

  public Object visit(ASTArrayAccess node, Object data) {
    Symbol sym;


    if(((ASTIndex)node.jjtGetChild(0)).getName() != null) {
        sym = new Symbol(((ASTIndex)node.jjtGetChild(0)).getName(),0, "variable");
        stack.add(sym);
    }
    else if(((ASTIndex)node.jjtGetChild(0)).getNumber() != null) {
        sym = new Symbol(((ASTIndex)node.jjtGetChild(0)).getNumber(),0, "constant");
        stack.add(sym);
    }
    sym = new Symbol(node.getName(),0, "arrayAccess");

    stack.add(sym);

    Object obj = super.visit(node, data);

    return obj;

  }


  public Object visit(ASTWhile node , Object data) {
	  Symbol sym= new Symbol("end_while", 0, "end_while");
	  stack.add(sym);

	  Object obj = new Object();
	  for(int i = node.jjtGetNumChildren()-1; i>=0; i--) {
		  if(node.jjtGetChild(i).getId() == YAL2JVMTreeConstants.JJTEXPRTEST)
			  obj=visit((ASTExprtest)(node.jjtGetChild(i)), data);
		  if(node.jjtGetChild(i).getId() == YAL2JVMTreeConstants.JJTSTMTLST)
			  obj=visit((ASTStmtlst)(node.jjtGetChild(i)), data);
	  }

	  sym = new Symbol("while", 0, "while");
	  stack.add(sym);

	  return obj;
  }

  public Object visit(ASTIf node , Object data) {
	  int nChildren=node.jjtGetNumChildren();

	  Object obj = new Object();
	  if(nChildren==2) {
		  //simple "if" statement
		  Symbol sym= new Symbol("end_if", 0, "end_if");
		  stack.add(sym);

		  if(node.jjtGetChild(nChildren-1).getId() == YAL2JVMTreeConstants.JJTSTMTLST)
			  obj=visit((ASTStmtlst)(node.jjtGetChild(nChildren-1)), data);
		  if(node.jjtGetChild(nChildren-2).getId() == YAL2JVMTreeConstants.JJTEXPRTEST)
			  obj=visit((ASTExprtest)(node.jjtGetChild(nChildren-2)), data);

		  sym = new Symbol("if", 0, "if");
		  stack.add(sym);

	  }else {
		  //"if" statement followed by "else" statement
		  Symbol sym= new Symbol("end_if", 0, "end_if");
		  stack.add(sym);


		  if(node.jjtGetChild(nChildren-1).getId() == YAL2JVMTreeConstants.JJTSTMTLST)
			  obj=visit((ASTStmtlst)(node.jjtGetChild(nChildren-1)), data);

		  sym = new Symbol("else", 0, "else");
		  stack.add(sym);

		  if(node.jjtGetChild(nChildren-2).getId() == YAL2JVMTreeConstants.JJTSTMTLST)
			  obj=visit((ASTStmtlst)(node.jjtGetChild(nChildren-2)), data);
		  if(node.jjtGetChild(nChildren-3).getId() == YAL2JVMTreeConstants.JJTEXPRTEST)
			  obj=visit((ASTExprtest)(node.jjtGetChild(nChildren-3)), data);

		  sym = new Symbol("if_else", 0, "if_else");
		  stack.add(sym);

	  }
	  return obj;
  }


  public Object visit(ASTExprtest node , Object data){
	  Symbol sym= new Symbol(node.getOp(),0, "condicOp");
	  stack.add(sym);

	  Object obj = super.visit(node, data);
	  return obj;
  }

}
