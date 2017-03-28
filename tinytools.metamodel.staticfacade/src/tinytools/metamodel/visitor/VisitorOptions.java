package tinytools.metamodel.visitor;

public class VisitorOptions {
	private String baseDir;
	private String packagePrefix;
	private String visitorClass = "AbstractVisitor";

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public void setPackagePrefix(String string) {
		this.packagePrefix = string;
	}
	
	public String getPackagePrefix() {
		return packagePrefix;
	}

	public String getVisitorClass() {
		return visitorClass ;
	}
	
	public void setVisitorClass(String visitorClass) {
		this.visitorClass = visitorClass;
	}

}
