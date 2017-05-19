package javaharjutused;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InterpretaatorTest2 {

	@Test
	public void test01_print() {
		check("print 42",
				
			"42\n");
		
		check("print 42\n"
			+ "print 12",
			
			  "42\n"
			+ "12\n");
	}

	@Test
	public void test02_muutujad() {
		check("x = 22\n"
			+ "print x",
			
			"22\n");
		
		check("KALA = 22\n"
			+ "KALA = 17\n"
			+ "print KALA",
			
			"17\n");
		
		check("KALA = 22\n"
			+ "y = 22\n"
			+ "KALA = 17\n"
			+ "print KALA\n"
			+ "print y",
			
			"17\n"
		  + "22\n");
	}
	
	@Test
	public void test03_tehtedArvudega() {
		check("print 2 + 2",
			
			"4");
		
		check("print 9 - 12",
			
			"-3");
		
		check("print 234234897 * 1",
			
			"234234897");
		
		check("print 12 / 3",
			
			"4");
		
		check("print 13 / 3",
			
			"4");
		
		check("print 12 % 3",
				
				"0");
			
			check("print 13 % 3",
				
				"1");
			
		check("print 13 == 13",
				
			"1");
		
		check("print 13 >= 13",
				
			"1");
	
		check("print 13 <= 13",
				
			"1");
		
		check("print 13 != 13",
			
			"0");
		
		check("print 13 < 13",
			
			"0");
		
		check("print 13 > 13",
		
			"0");
		
		
		check("print 13 == 14",
				
			"0");
		
		check("print 13 >= 14",
				
			"0");
	
		check("print 13 <= 14",
				
			"1");
		
		check("print 13 != 14",
			
			"1");
		
		check("print 13 < 14",
			
			"1");
		
		check("print 13 > 14",
		
			"0");
	}
	
	@Test
	public void test04_tehtedMuutujatega() {
		check("essa = 33\n"
			+ "tessa = 4\n"
			+ "kossa = essa - 2\n"
			+ "print kossa * essa\n"
			+ "print 0 - essa",
			
			"1023\n"
		 + "-33");
		
		check("essa = 3\n"
				+ "essa = essa + 1\n"
				+ "print essa",
				
				"4");
	}
	
	@Test
	public void test05_rohkemTühikuid() {
		check("essa = 33\n"
			+ "   tessa = 4\n"
			+ "kossa = essa      - 2\n"
			+ "  print               kossa *                essa              \n"
			+ "print 0 - essa",
			
			"1023\n"
		 + "-33");
	}
	
	@Test
	public void test06_tühjadReadJaKommentaarid() {
		check("\n"
			+ "essa = 33\n"
			+ "   tessa = 4\n"
			+ "kossa = essa      - 2 #komm\n"
			+ "\n"
			+ "    \n"
			+ "  print               kossa *                essa              \n"
			+ "  # kommentaar\n"
			+ "print 0 - essa # kommentaar 2",
			
			"1023\n"
		 + "-33");
	}
	
	@Test
	public void test07_erinevadMuutujanimed() {
		check("\n"
			+ "essa2 = 33\n"
			+ "   _tes_sa = 4\n"
			+ "_ = essa2      - 2 #komm\n"
			+ "\n"
			+ "    \n"
			+ "  print               _ *                essa2              \n"
			+ "  # kommentaar\n"
			+ "print 0 - essa2 # kommentaar 2",
			
			"1023\n"
		 + "-33");
		
		check("print_ = 2\n"
			+ "print print_",
			
				"2");
	}
	
	
	@Test
	public void test08_vead() {
		checkErr("prunt 1");
		checkErr("Print 1");
		checkErr("print 1.2");
		checkErr("print1");
		checkErr("print = 3");
		checkErr("y = print");
		checkErr("x = 1 + 3 + 2");
		checkErr("print 2 4");
		checkErr("print 2+4");
		checkErr("print 2+ 4");
		checkErr("print 2 +4");
		checkErr("print2 + 4");
		checkErr("print ");
		checkErr("4 + 3");
		checkErr("4");
		checkErr("kole");
		checkErr("kole = ");
		checkErr("kole = # 4");
		checkErr("kole = $4");
		checkErr("x = + 3");
		checkErr("3 = 4");
		checkErr("x = 4 + print");
		checkErr("3x = 4");
		
		checkErr("print x");
		checkErr("x = 3\nprint X");
	}
	
	@Test
	public void test09_kaksInterpretaatorit() {
		Interpretaator i1 = new Interpretaator();
		Interpretaator i2 = new Interpretaator();
		i1.käivitaLause("x = 1");
		i2.käivitaLause("x = 2");
		
		String out1 = captureStdout(() -> {i1.käivitaLause("print x");});
		String out2 = captureStdout(() -> {i2.käivitaLause("print x");});
		
		assertEquals("1", out1.trim());
		assertEquals("2", out2.trim());
	}
	
	@Test
	public void test10_demo_käivitaLause() {
		Interpretaator interpretaator = new Interpretaator();
		
		String out = captureStdout(() -> {
			interpretaator.käivitaLause("x = 4");
			interpretaator.käivitaLause("y = 10");
			interpretaator.käivitaLause("print x * y");
			interpretaator.käivitaLause("x = x + 1");
			interpretaator.käivitaLause("print x");
		});
		
		assertEquals("40\n5", out.replace("\r", "").trim());
	}
	
	@Test
	public void test11_demo_käivitaLause_eraldiMuutujad() {
		
		Map<String, Integer> muutujad = new HashMap<>();
		muutujad.put("x", 4);
		
		Interpretaator interpretaator = new Interpretaator(muutujad);
		
		String out = captureStdout(() -> {
			interpretaator.käivitaLause("y = 10");
			interpretaator.käivitaLause("print x * y");
			interpretaator.käivitaLause("x = x + 1");
			interpretaator.käivitaLause("print x");
		});
		
		assertEquals("40\n5", out.replace("\r", "").trim());
	}
	
	@Test
	public void test11_demo_käivitaProgramm() {
		
		List<String> read = new ArrayList<>();
		read.add("x = 4");
		read.add("y = 10");
		read.add("print x * y");
		read.add("x = x + 1");
		read.add("print x");
		
		Interpretaator interpretaator = new Interpretaator();
		
		String out = captureStdout(() -> {
			interpretaator.käivitaProgramm(read);
		});
		
		assertEquals("40\n5", out.replace("\r", "").trim());
	}
	
	@Test(timeout=2500)
	public void test12_demo_main() throws Exception {
		checkMain(new String[] {"demo.jaha"},
				"40\n5");
		checkMain(new String[] {"demo.jaha", "kala", "78"},
				"40\n5");
		checkMain(new String[] {"demo.jaha", "x", "78"},
				"40\n5");
	}
	
	@Test(timeout=2500)
	public void test13_abs_main() throws Exception {
		checkMain(new String[] {"abs.jaha", "x", "0"},
				"0");
		checkMain(new String[] {"abs.jaha", "x", "170"},
				"170");
		checkMain(new String[] {"abs.jaha", "x", "-1"},
				"1");
		checkMain(new String[] {"abs.jaha", "x", "-1", "y", "89"},
				"1");
	}
	
	@Test(timeout=2500)
	public void test14_faktoriaal_main() throws Exception {
		checkMain(new String[] {"faktoriaal.jaha", "n", "0"},
				"1");
		checkMain(new String[] {"faktoriaal.jaha", "n", "1"},
				"1");
		checkMain(new String[] {"faktoriaal.jaha", "n", "4"},
				"24");
	}
	
	@Test(timeout=2500)
	public void test15_vigane_programm() throws Exception {
		checkMainError(new String[] {"vigane.jaha", "n", "3"});
	}
	
	@Test(timeout=2500)
	public void test16_vigane_pöördumine() throws Exception {
		checkMainError(new String[] {"faktoriaal.jaha", "n"});
		checkMainError(new String[] {"faktoriaal.jaha", "n", "1", "y"});
		checkMainError(new String[] {"faktoriaal.jaha", "p", "4"});
	}
	
	private void checkMainError(String[] argumendid) throws Exception {
		ExecutionResult tulemus = runJavaProgramWithInput("javaharjutused.Interpretaator", "", argumendid);
		if (tulemus.err.isEmpty()) {
			fail("Interpretaator oleks pidanud andma vea");
		}
	}
	
	private void checkMain(String[] argumendid, String oodatavVäljund) throws Exception {
		ExecutionResult tulemus = runJavaProgramWithInput("javaharjutused.Interpretaator", "", argumendid);
		if (!tulemus.err.isEmpty()) {
			fail("Sain programmi käivitamisel vea: " + tulemus.err);
		}
		
		oodatavVäljund = oodatavVäljund.replace("\r", "").trim();
		String tegelikVäljund = tulemus.out.replace("\r",  "").trim();
		assertEquals(oodatavVäljund, tegelikVäljund);
	}
	
	private void check(String laused, String oodatavVäljund) {
		Interpretaator interpretaator = new Interpretaator();
		
		String tegelikVäljund = captureStdout(() -> {
			for (String lause : laused.split("\\n")) {
				interpretaator.käivitaLause(lause);
			}
		});
		
		assertEquals(oodatavVäljund.replace("\r", "").trim(), tegelikVäljund.replace("\r", "").trim());
	}
	
	private void checkErr(String laused) {
		Interpretaator interpretaator = new Interpretaator();
		
		try {
			for (String lause : laused.split("\\n")) {
				interpretaator.käivitaLause(lause);
			}
			fail("Järgnevad laused pidid andma RuntimeException-i: " + laused);
		} catch (RuntimeException e) {
			// Kõik korras. Pidigi andma erindi 
		}
	}

	private static String captureStdout(RunnableEx action) {
		PrintStream originalStdout = System.out;
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			System.setOut(new PrintStream(output));
			try {
				action.run();
				return output.toString();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} finally {
			System.setOut(originalStdout);
		}
	}

	private static interface RunnableEx {
		public void run() throws Exception;
	}

	public static ExecutionResult runJavaProgramWithInput(String className, String input, String... args) throws Exception {
		
		List<String> cmdParts = new ArrayList<String>();
		cmdParts.add("java");
		cmdParts.add("-mx128m");
		cmdParts.add("-Dfile.encoding=UTF-8");
		cmdParts.add("-cp");
		cmdParts.add(joinClasspath(".", "bin"));
		cmdParts.add(className);
		cmdParts.addAll(Arrays.asList(args));
	
		// Prepare input file
		File inputFile = File.createTempFile("test_input_", "");
		FileOutputStream out = new FileOutputStream(inputFile); 
		out.write(input.getBytes("UTF-8"));
		out.close();
		
		ProcessBuilder pb = new ProcessBuilder(cmdParts.toArray(new String[cmdParts.size()]));
		pb.redirectInput(inputFile);
		pb.redirectOutput(Redirect.PIPE);
		pb.redirectError(Redirect.PIPE);
		
		Process proc = pb.start();
		
		if (!input.isEmpty()) {
			// Send as much as possible
			for (byte b : input.getBytes(Charset.forName("UTF-8"))) {
				try {
					proc.getOutputStream().write(b);
				} catch (IOException e) {
					// TODO: in some cases this should fail
					break;
				}
			}
		}
		int returnCode = proc.waitFor();
		return new ExecutionResult(returnCode, 
				readAllFromStream(proc.getInputStream()),
				readAllFromStream(proc.getErrorStream()));
	}
	
	public static class ExecutionResult {
		public ExecutionResult(int returnCode, String out, String err) {
			this.out = out;
			this.err = err;
			this.returnCode = returnCode;
		}
		public String out;
		public String err;
		public int returnCode;
	}


	private static String readAllFromStream(InputStream stream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		StringBuilder sb = new StringBuilder();
		int ch;
		while ((ch = br.read()) != -1) {
			sb.append((char)ch);
		}
		return sb.toString();
	}
	
	private static String joinClasspath(String... parts) {
		String sep;
		if (System.getProperty("os.name").startsWith("Windows")) {
			sep = ";";
		} else {
			sep = ":";
		}
		
		StringBuilder sb = new StringBuilder();
		for (String part : parts) {
			sb.append(part);
			sb.append(sep);
		}
		
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);
		}
		
		return sb.toString();
	}
}
