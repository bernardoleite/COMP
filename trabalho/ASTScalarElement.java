/* Generated By:JJTree: Do not edit this line. ASTScalarElement.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTScalarElement extends SimpleNode {
  private String name;
  private Token t;

  public ASTScalarElement(int id) {
    super(id);
  }

  public ASTScalarElement(YAL2JVM p, int id) {
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
    if(currentTable.lookup(name) == null) {
      System.out.println("ScalarElement Save! " +name);
      currentTable.save(new Symbol(name, 0, "Variable"));
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
/* JavaCC - OriginalChecksum=382ecaae2db45d15c6102a11eef7dd86 (do not edit this line) */
