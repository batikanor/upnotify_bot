import java.io.ObjectInputFilter.Config;

import org.junit.Assert;
import org.junit.Test;

import objects.User;
import utils.DatabaseUtils;

public class GeneralTest {
    @Test
    public void parseIntegerTest() {


        String h = "";
        int hi = -1;
        try {
            hi = Integer.parseInt(h);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(hi);
        Assert.assertEquals(-1, hi); // means that empty string cannot be parsed as int
	
	}

    // @Test 
    // public void itemsTest() {
    //     User upUser = new User();
    //     upUser.telegramId = utils.Config.getConfig().

    //     for (objects.Request req : DatabaseUtils.getDatabaseUtils().getRequestsFromTelegramId(upUser.telegramId)){
	// 		if (req.requestId == num){
				
	// 		} else {
				
	// 		}
	// 	}
    // }

}
