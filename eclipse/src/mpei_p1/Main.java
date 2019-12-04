package mpei_p1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
	static int numNoRev, numRev;
	
	private static ArrayList<Game> jogos = new ArrayList<>();
	
	private static void teste() throws InterruptedException {
		System.out.println("------------------READING JSON------------------");
	 	long startTime = System.nanoTime();
		ReadJSON json = new ReadJSON();
		json.read();
		jogos = json.getJogos();
		
		long stopTime = System.nanoTime();
		long elapsedTime = stopTime-startTime;
		double elapsedTimeInSecond = (double) elapsedTime / 1_000_000_000;
		System.out.println("Time elapsed to read json (s): " + elapsedTimeInSecond+" seconds.");
	    
	    System.out.println("------------------CREATING BLOOM------------------");
	    startTime = System.nanoTime();

	    // CriaÁao do bloom e inserir a HashFunction para o bloom usar
		CBloom reviewsBloom = new CBloom(jogos.size(), 0.1);
		reviewsBloom.initialize();
		
		stopTime = System.nanoTime();
		elapsedTime = stopTime-startTime;
		elapsedTimeInSecond = (double) elapsedTime / 1_000_000_000;
		System.out.println("Time elapsed to create bloom (s): " + elapsedTimeInSecond+" seconds.");
		
		System.out.println("------------------Testes ao BLOOM------------------");
		TimeUnit.SECONDS.sleep(1);
		
		System.out.println("------------------Dados reais------------------");
		int numIns=0;
		for (int i=0;i<jogos.size();i++) {
        	ArrayList<Review> reviews = jogos.get(i).getReviews();
        	if (jogos.get(i).getName().equals("Might and Magic√Ç¬Æ 6-pack Limited Edition")) {
            	System.out.println("n de reviews reais de migth: "+reviews.size());
        	}
        	if (jogos.get(i).getName().equals("Galactic Civilizations III - Revenge of the Snathi DLC")) {
    			System.out.println("n de reviews reais de galatic: "+reviews.size());
    		}
        	if (jogos.get(i).getName().equals("Enclave")) {
    			System.out.println("n de reviews reais de enclave: "+reviews.size());
    		}

        	if (reviews.size() > 0) {
        		for (int j=0;j<reviews.size();j++) {
	            	reviewsBloom.insertEle(jogos.get(i).getName());
        		}
        		numIns++;
        	}
		}
		TimeUnit.SECONDS.sleep(1);
		System.out.println("------------------Dados do BLOOM------------------");
		System.out.println("enclave tem reviews? "+reviewsBloom.isEle("Enclave")); // tem de dar true
		System.out.println("galatic tem reviews? "+reviewsBloom.isEle("Galactic Civilizations III - Revenge of the Snathi DLC")); // tem de dar false
		System.out.println("might tem reviews? "+ reviewsBloom.isEle("Might and Magic√Ç¬Æ 6-pack Limited Edition")); // tem de dar true
		
		
		TimeUnit.SECONDS.sleep(1);
		System.out.println("enclave tem quantas reviews? "+reviewsBloom.numEle("Enclave")); // tem de dar 68
		System.out.println("galatic tem quantas reviews? "+reviewsBloom.numEle("Galactic Civilizations III - Revenge of the Snathi DLC")); // tem de dar 0
		System.out.println("migth tem quantas reviews? "+reviewsBloom.numEle("Might and Magic√Ç¬Æ 6-pack Limited Edition")); // tem de dar 133
		
		//reviewsBloom.deleteEle("Might and Magic√Ç¬Æ 6-pack Limited Edition");
		//System.out.println(reviewsBloom.isEle("Might and Magic√Ç¬Æ 6-pack Limited Edition")); // tem de dar false
		
		double pfp=Math.pow(1-Math.pow(1-1/reviewsBloom.getN(), reviewsBloom.getK()*reviewsBloom.getM()), reviewsBloom.getK());
		System.out.println("Probabilidade de falso positivo: " + pfp);
		
		int pos=0;
		for (int i=0;i<jogos.size();i++) {
			if (reviewsBloom.isEle(jogos.get(i).getName())) {
				pos++;
			}
		}
		
		System.out.println("Numero de elementos inseridos no bloom: " + numIns);
		System.out.println("Numero de elementos que estão no bloom: " + pos);
		System.out.println("Numero de colisoes: "+ (pos-numIns));
		
		
		System.out.println("------------------Testes à MinHash------------------");
		//display_menu();
				ArrayList<Review> reviews= new ArrayList<>();
				int size=0;
				for (Game jogo : jogos) {
					for(Review r: jogo.getReviews()) {
					size++;
			
					if (size<=1000) {
						reviews.add(r);
					}
				}}
				//System.out.println(reviews);

				MinHash lol = new MinHash(reviews, 10); //10 pq são frases grandes
				
				lol.printSimilars(0.5); // Limiar de distancia, qnt mais pequeno mais similar sao
	}
	
	public static void language() throws java.lang.InterruptedException {	    
		//Bloom para determinar se um jogo tem uma certa linguagem 
			   
		ArrayList<String> languages = new ArrayList<>();
	    HashMap<String, CBloom> linguagens = new HashMap<>(); 
	    HashMap<String, CBloom> linguagens_text = new HashMap<>(); 
	    HashMap<String, CBloom> linguagens_audio = new HashMap<>(); 
		for(Game jogo:jogos) {
			for(Language lang:jogo.getLanguages()) {
				if(!languages.contains(lang.getName())) {
					languages.add(lang.getName());
					//Bloom para determinar se um jogo tem uma certa linguagem 
					CBloom l = new CBloom(jogos.size(), 0.1); // bloom
					l.initialize();
					linguagens.put(lang.getName(), l);
					//Bloom para determinar se um jogo tem uma certa linguagem e nessa linguagem tem texto
					CBloom l_textBloom = new CBloom(jogos.size(), 0.1); // bloom 
					l_textBloom.initialize();
					linguagens_text.put(lang.getName(), l_textBloom);
					//Bloom para determinar se um jogo tem uma certa linguagem e nessa linguagem tem audio
					CBloom l_audioBloom = new CBloom(jogos.size(), 0.1); // bloom 
					l_audioBloom.initialize();
					linguagens_audio.put(lang.getName(), l_audioBloom);
				}
				
				CBloom l = linguagens.get(lang.getName());
				
				l.insertEle(jogo.getName());
				/*if(jogo.getName().equals("Galactic Civilizations III - Revenge of the Snathi DLC")) {
					System.out.println(lang.getName());
					System.out.println("É este");
					System.out.println(l.isEle("Galactic Civilizations III - Revenge of the Snathi DLC"));}*/
				
				CBloom l_textBloom = linguagens_text.get(lang.getName());
				l_textBloom.insertEle(jogo.getName());
				CBloom l_audioBloom = linguagens_audio.get(lang.getName());
				l_audioBloom.insertEle(jogo.getName());
				
			}
		}
		System.out.println(languages);
		
		
		TimeUnit.SECONDS.sleep(1);
		System.out.println("------------------Dados do BLOOM------------------");
		System.out.println("enclave tem Português do Brasil? "+linguagens.get("Português do Brasil").isEle("Enclave")); // tem de dar false
		System.out.println("enclave tem český? "+linguagens.get("český").isEle("Enclave")); // tem de dar false
		System.out.println("enclave tem nederlands? "+linguagens.get("nederlands").isEle("Enclave")); // tem de dar false
		System.out.println("galatic tem русский? "+linguagens.get("русский").isEle("Galactic Civilizations III - Revenge of the Snathi DLC")); // tem de dar true
		System.out.println("might tem norsk? "+ linguagens.get("norsk").isEle("Might and Magic® 6-pack Limited Edition")); // tem de dar false
			
		
		
	}
	
	public static void main(String[] args) throws java.lang.InterruptedException {
		// teste(); tests to bloom
		ReadJson();
		display_menu();
		
	}
		  
	

 static void display_menu() throws java.lang.InterruptedException {
	    System.out.println ( "1) Search by game\n2) See the similarities in the reviews of a game\n3) See our program data\n4) Test our program\n5) See Language Info" );
	    System.out.print ( "Selection: " );
	    
	    Scanner in = new Scanner ( System.in );
	    
		switch (in.nextInt()) {
			case 1:
		        System.out.println ( "You picked option 1" );
		        break;
			case 2:
				System.out.println ( "You picked option 2" );
				break;
			case 3:
				System.out.println("Our dataset contains "+jogos.size()+" games.");
				System.out.println("There are "+numRev+" games with reviews and "+(jogos.size()-numRev)+" without reviews in out dataset.");
				
				break;
			case 4:
				System.out.println ( "You picked option TESTES" );
		        teste();
			case 5:
				System.out.println ( "You picked option Language Info" );
		        language();
			default:
				System.err.println ( "Unrecognized option" );
				break;
	    }
		in.close();
	 }

	private static void ReadJson() {
		ReadJSON json = new ReadJSON();
		json.read();
		jogos = json.getJogos();
		
		CBloom reviewsBloom = new CBloom(jogos.size(), 0.1); // bloom to find out the games with reviews and how many
		reviewsBloom.initialize();	
		CBloom noReviewsBloom = new CBloom(jogos.size(), 0.1); // bloom 
		noReviewsBloom.initialize();
		
		numNoRev=0;
		numRev=0;
		for (int i=0;i<jogos.size();i++) {
        	ArrayList<Review> reviews = jogos.get(i).getReviews();
        	if (reviews.size() == 0) {
        		numNoRev++;
            	noReviewsBloom.insertEle(jogos.get(i).getName());
        	} else {
        		numRev++;
        		for (int j=0;j<reviews.size();j++) {
	            	reviewsBloom.insertEle(jogos.get(i).getName());
        		}
        	}
		}
		
		
	}
	
	public void InterruptedException() {
		System.out.println("Não consegui parar");
	}
}