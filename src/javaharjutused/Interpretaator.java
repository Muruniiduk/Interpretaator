package javaharjutused;

import org.omg.SendingContext.RunTime;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Interpretaator {
	private Map<String, Integer> muutujad = new HashMap<>();

	public Interpretaator(){
	}

	public Interpretaator(Map<String, Integer> muutujad){
		this.muutujad = muutujad;
	}
	
	public void käivitaLause(String lause) {
		if (lause.trim().startsWith("#") || lause.trim().isEmpty()) {
			return;
		}
		
		// otsin välja lause osad
		List<String> osad = new ArrayList<>();
		for (String osa : lause.split(" ")) {
			osa = osa.trim();
			if (osa.startsWith("#")) {
				break;
			}
			
			if (!osa.isEmpty()) {
				osad.add(osa.trim());
			}
		}
		
		if (osad.get(0).equals("print")) {
			System.out.println(arvuta(osad.subList(1, osad.size())));
		}
		else if (osad.size() < 3) {
			// kõik muud laused peale print-i peavad olema vähemalt 3 osaga
			throw new RuntimeException("Süntaksi viga: liiga vähe komponente");
		}
		else if (osad.get(1).equals("=")) {
			String nimi = osad.get(0);
			kontrolliMuutujaNime(nimi);
			int väärtus = arvuta(osad.subList(2, osad.size()));
			muutujad.put(nimi, väärtus);
		}
		else {
			throw new RuntimeException("Süntaksi viga (pole tühi, ega print ega omistamine)");
		}
	}

	public void käivitaProgramm (List<String> programmiRead){
		for(int i = 0; i < programmiRead.size(); i++){
			String rida = programmiRead.get(i);
			List<String> reaList = Arrays.asList(rida.split(" "));
			if(rida.contains("jump") && reaList.size() > 2 && !reaList.get(2).equals("if")){
				throw new RuntimeException("Syntaksi viga: kui pikkus yle 3, siis peab kolmas olema 'if' ");
			}
			if(rida.contains("jump") && reaList.size() > 2){
				try {
					if(arvuta(reaList.subList(2, reaList.size())) == 1){
						String voti = reaList.get(1);
						while(!programmiRead.get(i).equals(":".concat(voti))){
							i++;
						}
					}
				} catch (RuntimeException e) {
					throw new RuntimeException("Ifi j2rgne statement ei ole korralik");
				}
			}
			käivitaLause(rida);
		}
	}
	
	private int arvuta(List<String> osad) {
		if (osad.size() == 1) {
			return arvuta(osad.get(0));
		}
		else if (osad.size() == 2) {
			// kahe komponendiga avaldis saab olla ainult unaarse miinuse rakendamine
			if (! osad.get(0).equals("-")) {
				throw new RuntimeException();
			}
			return - arvuta(osad.get(1));
		}
		else if (osad.size() == 3) {
			int vasak = arvuta(osad.get(0));
			int parem = arvuta(osad.get(2));
			switch (osad.get(1)) {
			case "+": return vasak + parem;
			case "-": return vasak - parem;
			case "*": return vasak * parem;
			case "/": return vasak / parem;
			case "%": return vasak % parem;
			case "==" : return vasak == parem ? 1 : 0;
			case "!=" : return vasak != parem ? 1 : 0;
			case ">=" : return vasak >= parem ? 1 : 0;
			case "<=" : return vasak <= parem ? 1 : 0;
			case "<" : return vasak < parem ? 1 : 0;
			case ">" : return vasak > parem ? 1 : 0;
			default: throw new RuntimeException();
			}
		}
		else {
			throw new RuntimeException();
		}
	}
	
	private int arvuta(String jupp) {
		try {
			return Integer.parseInt(jupp);
		}
		catch (NumberFormatException e) {
			// ei olnud täisarv. Võib olla muutuja
			if (muutujad.containsKey(jupp)) {
				return muutujad.get(jupp);
			}
			else {
				kontrolliMuutujaNime(jupp);
				// kui see kontroll erindit ei visanud, siis vastas sõne muutuja reeglitele
				throw new RuntimeException("Defineerimata muutuja: " + jupp);
			}
		}
	}
	
	private void kontrolliMuutujaNime(String nimi) {
		nimi = nimi.toLowerCase();
		if (nimi.charAt(0) != '_' && !Character.isLetter(nimi.charAt(0))) {
			throw new RuntimeException("Muutuja nime alguses peab olema täht või allkriips");
		}
		
		for (char c : nimi.substring(1).toCharArray()) {
			if (!Character.isLetter(c) && !Character.isDigit(c) && c != '_') {
				throw new RuntimeException("Muutuja nimes võivad olla ainult tähed, allkriipsud ja numbrid");
			}
		}
	}

	public static void main(String[] args) throws IOException {


		Map<String, Integer> vm = new HashMap<>();

		Interpretaator interpretaator = new Interpretaator(vm);

		for(int i = 1; i < args.length; i=i+2){
			interpretaator.muutujad.put(args[i], Integer.parseInt(args[i+1]));
		}

		try (Scanner sc = new Scanner(new File(args[0]), "UTF-8")) {
			List<String> read = new ArrayList<>();
			while (sc.hasNextLine()) {
				read.add(sc.nextLine());
			}
			interpretaator.käivitaProgramm(read);
		}


	}
	

}
