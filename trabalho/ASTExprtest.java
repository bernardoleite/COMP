/* Generated By:JJTree: Do not edit this line. ASTExprtest.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTExprtest extends SimpleNode {
  private String op;

  public ASTExprtest(int id) {
    super(id);
    this.op="";
  }

  public ASTExprtest(YAL2JVM p, int id) {
    super(p, id);
    this.op="";
  }
  
  public void setOp(String op) {
    this.op=op;
  }

  public String toString() {
    String print=super.toString();
    print+=" "+op;
    return print;
  }
  
  public String getOp() {
	  return op;
  }


  /** Accept the visitor. **/
  public Object jjtAccept(YAL2JVMVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=1866d65c99c9b1c31582fddef7b99b3d (do not edit this line) */
