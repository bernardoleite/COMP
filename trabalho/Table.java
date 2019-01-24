import java.util.*;
public class Table {
  private Table parentTable;

	private HashMap<String,Symbol> symbols = new HashMap<String,Symbol>();


  public Table(Table parentTable) {
    this.parentTable = parentTable;
    this.symbols = new HashMap<String,Symbol>();
  }

  public Symbol lookup(String symbol_name) {
      if(this.symbols.containsKey(symbol_name))
        return this.symbols.get(symbol_name);
      else if(this.parentTable != null)
        return this.parentTable.lookup(symbol_name);
      return null;
  }
  
  public void save(Symbol symbol) {
      this.symbols.put(symbol.name, symbol);
  }

}
