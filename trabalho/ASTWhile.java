/* Generated By:JJTree: Do not edit this line. ASTWhile.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTWhile extends SimpleNode {
  public ASTWhile(int id) {
    super(id);
  }

  public ASTWhile(YAL2JVM p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(YAL2JVMVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=d89d48ef30e67b124f8c0d104d7bd97d (do not edit this line) */
