public class Symbol implements Cloneable{
	public String type;
	public String name;
	public int scalarOrArray;

    public Symbol(String name, int scalarOrArray, String type) {
      this.type = type;
      this.scalarOrArray = scalarOrArray;
      this.name = name;
    }

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

  	public int getScalarOrArray() {
		return scalarOrArray;
	}
  	
  	public void changeNameToLowerCase() {
  		this.name=this.name.toLowerCase();
  		return;
  	}

	/*@overrides
	public Symbol clone(){
		final Y clone;
		try{
			clone = (Y) super.clone();
		}
		catch(CloneNoteSupportedException ex){
			throw new RuntimeException("impossible to clone", ex);
		}

		clone.
	}*/


}
