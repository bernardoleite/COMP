/* Generated By:JJTree: Do not edit this line. ASTAssign.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTAssign extends SimpleNode {
  public ASTAssign(int id) {
    super(id);
  }

  public ASTAssign(YAL2JVM p, int id) {
    super(p, id);
  }

  public boolean analyse(Table currentTable) {
   int j = this.jjtGetChild(1).checkScalarOrArray(currentTable);


   boolean b = this.jjtGetChild(0).analyse(currentTable, j);
   if(j==-1)
    return false;
   return b;
  }


  /** Accept the visitor. **/
  public Object jjtAccept(YAL2JVMVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=ab258d829077f17b7635f430b58b931d (do not edit this line) */
