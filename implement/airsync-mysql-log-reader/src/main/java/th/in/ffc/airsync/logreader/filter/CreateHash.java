/*
 * Copyright (c) 2018 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package th.in.ffc.airsync.logreader.filter;

import org.apache.commons.codec.digest.DigestUtils;
import th.in.ffc.airsync.logreader.QueryRecord;

public class CreateHash implements Filters {

    @Override
    public QueryRecord process(QueryRecord record) {
        record.setHash(DigestUtils.sha1Hex(record.getLog()+ record.getTime()+ record.getLinenumber()));
        return record;
    }
}
