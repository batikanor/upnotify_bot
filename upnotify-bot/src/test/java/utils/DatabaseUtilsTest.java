package utils;

import java.util.Arrays;
import java.util.List;


import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import utils.Config;
import utils.DatabaseUtils;

public class DatabaseUtilsTest {

    @Test
    public void check_tables(){
        DatabaseUtils.create_tables();
    }


}
