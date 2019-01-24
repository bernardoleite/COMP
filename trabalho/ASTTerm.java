/* Generated By:JJTree: Do not edit this line. ASTTerm.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTTerm extends SimpleNode {
  private String op = "+";
  private String name;
  public ASTTerm(int id) {
    super(id);
  }

  public ASTTerm(YAL2JVM p, int id) {
    super(p, id);
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getOp() {
    return op;
  }
  public void setOp(String op) {
    this.op = op;
  }
  public String toString() {
    String test = super.toString();
    if(name != null)
      return test + " " + op + " " + name;
    return test + " " + op;
  }

  public int checkScalarOrArray(Table currentTable) {
    if(name == null)
      return this.jjtGetChild(0).checkScalarOrArray(currentTable);
    else {
      return 0;
    }
  }

  /** Accept the visitor. **/
  public Object jjtAccept(YAL2JVMVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=5d3a68fdda66f7b9326bf5f7cb948322 (do not edit this line) */
