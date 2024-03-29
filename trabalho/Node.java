/* Generated By:JJTree: Do not edit this line. Node.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
/* All AST nodes must implement this interface.  It provides basic
   machinery for constructing the parent and child relationships
   between nodes. */

public
interface Node {

  public boolean analyse(Table currentTable);
  public boolean analyse(Table currentTable, int j);
  public boolean analyseLeft(Table currentTable, int j);
  public boolean analyseLeft(Table currentTable);
  public boolean prepareModule(Table currentTable);
  public int checkScalarOrArray(Table currentTable);

  /** This method is called after the node has been made the current
    node.  It indicates that child nodes can now be added to it. */
  public void jjtOpen();

  /** This method is called after all the child nodes have been
    added. */
  public void jjtClose();

  /** This pair of methods are used to inform the node of its
    parent. */
  public void jjtSetParent(Node n);
  public Node jjtGetParent();

  /** This method tells the node to add its argument to the node's
    list of children.  */
  public void jjtAddChild(Node n, int i);

  /** This method returns a child node.  The children are numbered
     from zero, left to right. */
  public Node jjtGetChild(int i);

  /** Return the number of children the node has. */
  public int jjtGetNumChildren();

  public int getId();

  /** Accept the visitor. **/
  public Object jjtAccept(YAL2JVMVisitor visitor, Object data);
}
/* JavaCC - OriginalChecksum=4905ab1605f05d22aa1e6a1105b2b28d (do not edit this line) */
