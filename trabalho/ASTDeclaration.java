/* Generated By:JJTree: Do not edit this line. ASTDeclaration.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTDeclaration extends SimpleNode {

  private int number;
  private String op;

  public ASTDeclaration(int id) {
    super(id);
  }

  public ASTDeclaration(YAL2JVM p, int id) {
    super(p, id);
  }
  public void setNumber(String number) {
    this.number = Integer.parseInt(number);
  }
  public void setOp(String op) {
    this.op = op;
  }
  public String toString() {
    String test = super.toString();
    if(this.op != null)
      return test + " " + op + " " + number;
    else
      return test + " " + number;

  }

  public boolean prepareModule(Table currentTable) {
    boolean x = true;
    for(int i = 0; i < this.jjtGetNumChildren(); i++) {
      boolean b = this.jjtGetChild(i).analyse(currentTable);
      if(!b)
        x = false;
    }
    return x;
  }
  /** Accept the visitor. **/
  public Object jjtAccept(YAL2JVMVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=6c027e63e87dd5075327cdd5753fdb24 (do not edit this line) */
