import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class LogParser {
	private static final int ITERATION_IDX = 0;
	private static final int TIMESTAMP_IDX = 3;
	private static final int THREAD_IDX = 2;
	private static final int EVENT_IDX = 4;
	private static final int ACTION_IDX = 5;
	private static final int STATE_IDX = 6;
	private static final int ACTIONS_IDX = 7;
	private static final String DEL_EXPLORED = "DEL_EXPLORED";
	private static final String DEL_CUTOFF = "DEL_CUTOFF";
	private static final String POP = "POP";
	private static final String PUSH = "PUSH";
	private static final String GOAL = "GOAL";

	private final List<List<StackEntry>> stacks;
	private final List<List<StackEntry>> initialStacks;
	private final List<TimestampEntry> timestamps;
	private final int nThreads;
	private final int nTimestamps;
	private final int branchingFactor;
	private final BufferedReader buffer;
	private int currentTime;
	private int currentIteration;
	
	private class TimestampEntry {
		public String[] lines;
		public int iteration;
	}
	
	public LogParser(File file) throws IOException {
		buffer = new BufferedReader( new FileReader(file));
		System.out.println(buffer.ready());
		
		nThreads = Integer.parseInt(buffer.readLine());
		nTimestamps = Integer.parseInt(buffer.readLine());
		branchingFactor = Integer.parseInt(buffer.readLine());
		stacks = new ArrayList<List<StackEntry>>();
		initialStacks = new ArrayList<List<StackEntry>>();
		timestamps = new ArrayList<TimestampEntry>();
		currentTime = -1;
		currentIteration = -1;

		for( int tid=0; tid<nThreads; tid++) {
			String[] line = buffer.readLine().split(",");
			List<StackEntry> stacki = new ArrayList<StackEntry>();
			for( int i=0; i<line.length; ) {
				String action = line[i++];
				String state = line[i++];
				int nactions = Integer.parseInt(line[i++]);
				Set<String> actions = new HashSet<String>();
				while( nactions-- > 0) actions.add(line[i++]);
				StackEntry entry = new StackEntry(action,state,actions);
				stacki.add(entry);
			}
			initialStacks.add(stacki);
			stacks.add(new ArrayList<StackEntry>());
		}
		resetStacks();
	}

	private void doLine(final String lineArr[]) {
		int tid = Integer.parseInt(lineArr[THREAD_IDX]);
		String event = lineArr[EVENT_IDX];
		
		if( event.equals(DEL_CUTOFF) || event.equals(DEL_EXPLORED)) {
			List<StackEntry> stack = stacks.get(tid);
			StackEntry entry = stack.get(stack.size()-1);
			entry.getActions().remove(lineArr[ACTION_IDX]);
		}
		else if( event.equals(PUSH)) {
			List<StackEntry> stack = stacks.get(tid);
			String action = lineArr[ACTION_IDX];
			String state = lineArr[STATE_IDX];
			Set<String> actions = new HashSet<String>();
			for( int j=ACTIONS_IDX; j<lineArr.length; j++) actions.add(lineArr[j]);
			StackEntry newEntry = new StackEntry(action,state,actions);
			stack.get(stack.size()-1).getActions().remove(action);
			stack.add(newEntry);
		}
		else if( event.equals(POP)) {
			List<StackEntry> stack = stacks.get(tid);
			stack.remove(stack.size()-1);
		}
		else if( event.equals(GOAL)) {
			
		}
		else
			System.err.println("Didn't understand event: "+event);
	}

	private void undoLine( final String line) {
		if( line == null) return;
		String lineArr[] = line.split(",");
		int tid = Integer.parseInt(lineArr[THREAD_IDX]);
		String event = lineArr[EVENT_IDX];
		
		if( event.equals(DEL_CUTOFF) || event.equals(DEL_EXPLORED)) {
			String action = lineArr[ACTION_IDX];
			List<StackEntry> stack = stacks.get(tid);
			stack.get(stack.size()-1).getActions().add(action);
		}
		else if( event.equals(PUSH)) {
			String action = lineArr[ACTION_IDX];
			List<StackEntry> stack = stacks.get(tid);
			stack.remove(stack.size()-1);
			stack.get(stack.size()-1).getActions().add(action);
		}
		else if( event.equals(POP)) {
			List<StackEntry> stack = stacks.get(tid);
			String action = lineArr[ACTION_IDX];
			String state = lineArr[STATE_IDX];
			Set<String> actions = new HashSet<String>();
			for( int j=ACTIONS_IDX; j<lineArr.length; j++) actions.add(lineArr[j]);
			StackEntry newEntry = new StackEntry(action,state,actions);
			stack.add(newEntry);
		}
		else if( event.equals(GOAL)) {
			
		}
		else
			System.err.println("Didn't understand event: "+event);
		
		
	}

	private void doLine(final String line) {
		if( line == null) return;
		doLine(line.split(","));
	}

	// iteration,cutoff,thread,timestamp,event
	public void reverseTime( int delta) {
		int targetTime = currentTime - delta;
		
		if( targetTime < 0) {
			resetStacks();
			currentTime = -1;
			currentIteration = -1;
			return;
		}
		
		while( currentTime > targetTime) {
			TimestampEntry previousEntry = timestamps.get(currentTime-1);
			TimestampEntry entry = timestamps.get(currentTime);
			if( previousEntry.iteration != currentIteration) {
				for( List<StackEntry> stack : stacks)
					stack.clear();

				currentIteration = previousEntry.iteration;
			}
			else {
				for( int i=0; i<nThreads; i++)
						undoLine( entry.lines[i]);
			}

			currentTime--;
		}
		
	}
	
	private void resetStacks() {
		for( int j=0; j<stacks.size(); j++) {
			List<StackEntry> stack = stacks.get(j);
			stack.clear();
			for( StackEntry entry : initialStacks.get(j))
				stack.add((StackEntry) entry.clone());
		}
	}
	
	public void advanceTime(int delta) throws IOException {
		int targetTime = currentTime + delta;

		while( currentTime+1 < timestamps.size() && currentTime < targetTime) {
			currentTime++;
			TimestampEntry timestampEntry = timestamps.get(currentTime);
			String lines[] = timestampEntry.lines;
			if( timestampEntry.iteration != currentIteration) {
				resetStacks();
				currentIteration = timestampEntry.iteration;
			}
			for( int i=0; i<nThreads; i++)
				doLine(lines[i]);
		}

		String line;
		String lineArr[] = null;
		int lineTime = 0, lineTid = 0, lineIteration = 0;
		while( currentTime < targetTime) {
			do {
				buffer.mark(500);
				line = buffer.readLine();
				if( line != null) {
					lineArr = line.split(",");
					lineIteration = Integer.parseInt(lineArr[ITERATION_IDX]);
					lineTime = Integer.parseInt(lineArr[TIMESTAMP_IDX]);
					lineTid = Integer.parseInt(lineArr[THREAD_IDX]);
				}
				else
					lineTime = Integer.MAX_VALUE;
				
				if( lineTime == currentTime+1) {
					if( lineTime == timestamps.size()) {
						TimestampEntry entry = new TimestampEntry();
						entry.lines = new String[nThreads];
						entry.iteration = lineIteration;
						timestamps.add(entry);
					}
					timestamps.get(lineTime).lines[lineTid] = line;
					if( lineIteration != currentIteration) {
						resetStacks();
						currentIteration = lineIteration;
					}
					doLine( lineArr);
				}
				else
					buffer.reset();
			} while( lineTime == currentTime + 1);
			currentTime++;
		}
	}
	
	public List<StackEntry> getStack( int tid) {
		return stacks.get(tid);
	}

	public int getNThreads() {
		return nThreads;
	}

	public int getNTimestamps() {
		return nTimestamps;
	}

	public int getCurrentTime() {
		return currentTime;
	}

	public int getBranchingFactor() {
		return branchingFactor;
	}

	public int getCurrentIteration() {
		return currentIteration;
	}

	public void setCurrentIteration(int currentIteration) {
		this.currentIteration = currentIteration;
	}	
}
