package in.nitish.helper;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class RandomNumGenerate {

	
	public int generaterandomNumber(int range) {
		
		Random random=new Random();
		int randomNumber = random.nextInt(range);
		System.out.println("Generated Random Number : " +randomNumber);
		return randomNumber;
		
	}
	
}
