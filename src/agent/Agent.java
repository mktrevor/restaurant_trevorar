package agent;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Base class for simple agents
 */
public abstract class Agent {
    Semaphore stateChange = new Semaphore(1, true);
    Semaphore pause = new Semaphore(0, true);
    boolean isPaused = false;
    private AgentThread agentThread;

    protected Agent() {
    }

    /**
     * This should be called whenever state has changed that might cause
     * the agent to do something.
     */
    protected void stateChanged() {
        stateChange.release();
    }

    /**
     * Agents must implement this scheduler to perform any actions appropriate for the
     * current state.  Will be called whenever a state change has occurred,
     * and will be called repeated as long as it returns true.
     *
     * @return true iff some action was executed that might have changed the
     *         state.
     */
    protected abstract boolean pickAndExecuteAnAction();

    /**
     * Return agent name for messages.  Default is to return java instance
     * name.
     */
    protected String getName() {
        return StringUtil.shortName(this);
    }

    /**
     * The simulated action code
     */
    protected void Do(String msg) {
        print(msg, null);
    }

    /**
     * Print message
     */
    protected void print(String msg) {
        print(msg, null);
    }

    /**
     * Print message with exception stack trace
     */
    protected void print(String msg, Throwable e) {
        StringBuffer sb = new StringBuffer();
        sb.append(getName());
        sb.append(": ");
        sb.append(msg);
        sb.append("\n");
        if (e != null) {
            sb.append(StringUtil.stackTraceString(e));
        }
        System.out.print(sb.toString());
    }

    /**
     * Start agent scheduler thread.  Should be called once at init time.
     */
    public synchronized void startThread() {
        if (agentThread == null) {
            agentThread = new AgentThread(getName());
            agentThread.start(); // causes the run method to execute in the AgentThread below
        } else {
            agentThread.interrupt();//don't worry about this for now
        }
    }

    /**
     * Stop agent scheduler thread.
     */
    //In this implementation, nothing calls stopThread().
    //When we have a user interface to agents, this can be called.
    public void stopThread() {
        if (agentThread != null) {
            agentThread.stopAgent();
            agentThread = null;
        }
    }

    /**
     * Agent scheduler thread, calls respondToStateChange() whenever a state
     * change has been signalled.
     */
    private class AgentThread extends Thread {
        private volatile boolean goOn = false;

        private AgentThread(String name) {
            super(name);
        }

        public void run() {
            goOn = true;

            while (goOn) {
                try {
                    // The agent sleeps here until someone calls, stateChanged(),
                    // which causes a call to stateChange.give(), which wakes up agent.
                    stateChange.acquire();
                    //The next while clause is the key to the control flow.
                    //When the agent wakes up it will call respondToStateChange()
                    //repeatedly until it returns FALSE.
                    //You will see that pickAndExecuteAnAction() is the agent scheduler.
                    while (pickAndExecuteAnAction()) {
                    	if(isPaused) {
                    		pause.acquire();
                    	}
                    }
                } catch (InterruptedException e) {
                    // no action - expected when stopping or when deadline changed
                } catch (Exception e) {
                    print("Unexpected exception caught in Agent thread:", e);
                }
            }
        }

        private void stopAgent() {
            goOn = false;
            this.interrupt();
        }
    }
    
    public void pause() {
    	isPaused = true;
    }
    
    public void resume() {
    	pause.release();
    	isPaused = false;
    	stateChanged();
    }
    
  //Code for food and menu classes    
    public static class Menu {
    	static List<Choice> choices = new ArrayList<Choice>();
    	
    	public Menu() {
    		choices.add(new Choice("steak", 15.99));
    		choices.add(new Choice("fish", 13.99));
    		choices.add(new Choice("chicken", 10.99));
    	}
    	
    	public Choice getChoice(int index) {
    		return choices.get(index);
    	}
    	
    	public int getMenuSize() {
    		return choices.size();
    	}
    }
    
    public static class Choice {
    	String type;
    	double price;
    	
    	public Choice(String type, double price) {
    		this.type = type;
    		this.price = price;
    	}
    	
    	public String getType() {
    		return type;
    	}
    	
    	public double getPrice() {
    		return price;
    	}
    }
}

