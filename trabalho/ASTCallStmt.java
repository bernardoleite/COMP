/* Generated By:JJTree: Do not edit this line. ASTCallStmt.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTCallStmt extends SimpleNode {
  private String Id1;
  private String Id2;
  private Token t;

  public ASTCallStmt(int id) {
    super(id);
    this.Id1="";
    this.Id2="";
  }

  public ASTCallStmt(YAL2JVM p, int id) {
    super(p, id);
    this.Id1="";
    this.Id2="";
  }

  public String getId1 () {
      return Id1;
  }

    public String getId2 () {
      return Id2;
  }

  public void setToken(Token t) {
    this.t = t;
  }

  //<ID> ( "." <ID> )? <LPAR> ( ArgumentList() )? <RPAR>
  public void setId1 (String Id1) {
    this.Id1=Id1;
  }

  public void setId2 (String Id2) {
    this.Id2=Id2;
  }

  public String toString() {
  String print=super.toString()+" >>"+Id1;
  if(Id2!="") {
    print+="."+Id2;
  }
  print+="(...)";
  return print;
  }

  @Override
 public boolean analyse(Table currentTable) {

    if(Id2 == null)
    {
      Symbol symbol = currentTable.lookup(Id1);
      if(symbol == null) {
        System.out.println("Variable or Function Doesn't exist! " +Id1 + " at line: " + t.beginLine);
        return false;
      }
      if(!symbol.getType().equals("Function")) {
          System.out.println("Isn't an Function! " +Id1 + " at line: " + t.beginLine);
          return false;
      }
      return true;
    }
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
/* JavaCC - OriginalChecksum=0f637ce97f90cd5d825523350e9a0f3a (do not edit this line) */
