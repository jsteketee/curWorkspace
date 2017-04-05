
public class Clause {
	Literal[] lits = new Literal[3];

	public Clause(Literal a, Literal b, Literal c) {
		lits[0] = a;
		lits[1] = b;
		lits[2] = c;
	}
}
