/* Generated By:JJTree: Do not edit this line. ASTIndex.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTIndex extends SimpleNode {
  private String number;
  private String name;
  private Token t;

  public ASTIndex(int id) {
    super(id);
  }

  public ASTIndex(YAL2JVM p, int id) {
    super(p, id);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public void setToken(Token t) {
    this.t = t;
  }

  public String toString() {
    String test = super.toString();
    if(this.name != null)
      test += " " + name;
    if(this.number != null)
      test += " " + number;
    return test;
  }

  @Override
 public boolean analyse(Table currentTable) {

    if(this.name != null)
    {
      Symbol var = currentTable.lookup(name);
      if(var == null) {
        System.out.println("Variable doesn't exist" +name + " at line: " + t.beginLine);
        return false;
      }
      if(!var.getType().equals("Variable")) {
        System.out.println("Isn't an Variable" +name + " at line: " + t.beginLine);
        return false;
      }
      if(var.getScalarOrArray() != 0) {
        System.out.println("Variable isn't scalar "+name + " at line: " + t.beginLine);
        return false;
      }
    }

    return true;
  }


  /** Accept the visitor. **/
  public Object jjtAccept(YAL2JVMVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=cd51abc3f28841883e804f062ca53fe8 (do not edit this line) */
