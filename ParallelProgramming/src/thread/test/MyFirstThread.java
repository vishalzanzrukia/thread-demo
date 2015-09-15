package thread.test;

class A {
	   private static B b = null;
	   public A() {
	       if (b == null)
	         b = new B();
	   }

	   void f1() {
	         b.func();
	   }
	}

class B{
	void func(){
		System.out.println("func");
	}
	
	public static void main(String[] args) {
		A aa = new A();
		aa.f1();
		aa = new A();
		aa.f1();
	}
}

public class MyFirstThread {
	public static void main(String[] args) {
		// dmlzaGFsLnphbnpydWtpYTpJLGFtLGdyZWF0
		// curl -D- -X GET -H
		// "Authorization: Basic dmlzaGFsLnphbnpydWtpYTpJLGFtLGdyZWF0" -H
		// "Content-Type: application/json" "http://rsjs001:8080/browse/AM-10"
		// -o D:\me\learning\jira-api\test.html
	}
}

class MyRunnableThread implements Runnable {

	public static int myCount = 0;

	public MyRunnableThread() {

	}

	public void run() {
		while (MyRunnableThread.myCount <= 10) {
			try {
				System.out.println("Expl Thread: "
						+ (++MyRunnableThread.myCount));
				Thread.sleep(100);
			} catch (Exception iex) {
				System.out.println("Exception in thread: " + iex.getMessage());
			}
		}
	}
}

class RunMyThread {
	public static void main(String a[]) throws InterruptedException {
		System.out.println("Starting Main Thread...");
		MyRunnableThread mrt = new MyRunnableThread();
		Thread t = new Thread(mrt);
		t.start();
		while (MyRunnableThread.myCount <= 10) {
			try {
				System.out.println("Main Thread: "
						+ (++MyRunnableThread.myCount));
				// Thread.sleep(100);
			} catch (Exception iex) {
				System.out.println("Exception in main thread: "
						+ iex.getMessage());
			}
		}
	}
}