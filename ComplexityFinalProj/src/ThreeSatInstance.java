import java.util.Random;

public class ThreeSatInstance {
	Clause[] clauses;
	int numOfVars;
	int numOfClauses;

	public ThreeSatInstance(int numOfVars, int numOfClauses, Clause[] clauses) {
		this.clauses = clauses;
		this.numOfClauses = numOfClauses;
		this.numOfVars = numOfVars;
	}

	public ThreeSatInstance(int numOfVars, int numOfClauses) {
		this.numOfVars = numOfVars;
		this.numOfClauses = numOfClauses;
		clauses = new Clause[numOfClauses];
		Random rand = new Random();
		for (int i = 0; i < numOfClauses; i++) {
			for (int j = 0; j < 3; j++) {
				clauses[i].lits[j] = new Literal(rand.nextInt(numOfVars), rand.nextBoolean());
			}
		}
	}
}
