/* Generated By:JJTree: Do not edit this line. ASTArrayAccess.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTArrayAccess extends SimpleNode {

  private String name;
  private Token t;

  public ASTArrayAccess(int id) {
    super(id);
  }

  public ASTArrayAccess(YAL2JVM p, int id) {
    super(p, id);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setToken(Token t) {
    this.t = t;
  }

  public String toString() {
    String test = super.toString();
    return test + " " + name;
  }

  @Override
  public boolean analyse(Table currentTable) {
    Symbol var = currentTable.lookup(name);
    if(var == null) {
      System.out.println("Variable Doesn't exist! " +name + " at line: " + t.beginLine);
      return false;
    }
    if(!var.getType().equals("Variable")) {
      System.out.println("Isn't a Variable " +name + " at line: " + t.beginLine);
      return false;
    }

    if(var.getScalarOrArray() != 1 ) {
      System.out.println("Variable isn't an array! " +name + " at line: " + t.beginLine);
      return false;
    }
    return true;
  }

    @Override
  public boolean analyseLeft(Table currentTable, int j) {
    Symbol var = currentTable.lookup(name);
    if(var == null) {
      System.out.println("Saving ASTArrayAccess " +name);
      currentTable.save(new Symbol(name, j, "Variable"));
      return true;
    }
    return true;
  }

  public int checkScalarOrArray(Table currentTable) {
    return 0;
  }

  /** Accept the visitor. **/
  public Object jjtAccept(YAL2JVMVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=4603a3edc19402c8c17e360ae88b9955 (do not edit this line) */
