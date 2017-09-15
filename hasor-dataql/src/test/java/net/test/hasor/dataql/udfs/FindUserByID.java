/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.test.hasor.dataql.udfs;
import net.hasor.dataql.Option;
import net.hasor.dataql.UDF;
import net.hasor.utils.json.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * @version : 2014-7-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class FindUserByID implements UDF {
    @Override
    public Object call(Object[] values, Option readOnly) {
        System.out.println("FindUserByID -> params : " + JSON.toString(values));
        //
        ArrayList<Map<String, Object>> addressSet = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 5; i++) {
            HashMap<String, Object> udfData = new HashMap<String, Object>();
            udfData.put("zip", "1234" + i);
            udfData.put("code", "c_" + i);
            udfData.put("address", "this is detail address info.");
            addressSet.add(udfData);
        }
        //
        HashMap<String, Object> udfData = new HashMap<String, Object>();
        udfData.put("name", "this is name.");
        udfData.put("name2", "this is name2.");
        udfData.put("age", 31);
        udfData.put("nick", "this is nick.");
        udfData.put("userID", 1111111);
        udfData.put("status", true);
        udfData.put("addressList", addressSet);
        return udfData;
    }
}