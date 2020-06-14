/*
 * This work is dual-licensed
 * - under the Apache Software License 2.0 (the "ASL")
 * - under the jOOQ License and Maintenance Agreement (the "jOOQ License")
 * =============================================================================
 * You may choose which license applies to you:
 *
 * - If you're using this work with Open Source databases, you may choose
 *   either ASL or jOOQ License.
 * - If you're using this work with at least one commercial database, you must
 *   choose jOOQ License
 *
 * For more information, please visit http://www.jooq.org/licenses
 *
 * Apache Software License 2.0:
 * -----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * jOOQ License and Maintenance Agreement:
 * -----------------------------------------------------------------------------
 * Data Geekery grants the Customer the non-exclusive, timely limited and
 * non-transferable license to install and use the Software under the terms of
 * the jOOQ License and Maintenance Agreement.
 *
 * This library is distributed with a LIMITED WARRANTY. See the jOOQ License
 * and Maintenance Agreement for more details: http://www.jooq.org/licensing
 */
package org.jooq.mcve.test;

import static org.jooq.impl.DSL.cast;
import static org.jooq.mcve.Tables.TEST;
import static org.jooq.util.postgres.PostgresDSL.arrayOverlap;

import java.sql.Connection;
import java.sql.DriverManager;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.mcve.enums.AccessRight;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MCVETest {

    Connection connection;
    DSLContext ctx;

    @Before
    public void setup() throws Exception {
        connection = DriverManager.getConnection("jdbc:postgresql:somedb", "someuser", "mcve");
        ctx = DSL.using(connection);
    }

    @After
    public void after() throws Exception {
        ctx = null;
        connection.close();
        connection = null;
    }

    @Test
    public void enumArrayNoCast() {
        ctx.selectFrom(TEST)
            .where(arrayOverlap(TEST.ACCESS_RIGHTS, new AccessRight[]{AccessRight.administrator}))
            .fetch();
    }

    @Test
    public void enumArrayWithCast() {
        ctx.selectFrom(TEST)
            .where(arrayOverlap(TEST.ACCESS_RIGHTS, cast(new AccessRight[]{AccessRight.administrator}, TEST.ACCESS_RIGHTS)))
            .fetch();
        // Will fail with
        //   ERROR: type "access_right[]" does not exist(..)
        // because it generates
        //   cast(?::"mcve"."access_right"[] as access_right[])
        // the second access_right should be qualified
    }

    @Test(expected = DataAccessException.class)
    public void domainArrayNoCast() {
        ctx.selectFrom(TEST)
            .where(arrayOverlap(TEST.EXTERNAL_IDS, new String[]{"foo"}))
            .fetch();
        // Will fail with
        //   ERROR: operator does not exist: mcve.external_id[] && character varying[](..)
        // because it generates
        //   ?::varchar[]
        // This is an expected failure.
    }

    @Test
    public void domainArrayWithCast() {
        ctx.selectFrom(TEST)
            .where(arrayOverlap(TEST.EXTERNAL_IDS, cast(new String[]{"foo"}, TEST.EXTERNAL_IDS)))
            .fetch();
    }

    @Test
    public void enumNoCast() {
        ctx.selectFrom(TEST)
            .where(TEST.ACCESS_RIGHT.eq(AccessRight.administrator))
            .fetch();
    }

    @Test
    public void enumWithCast() {
        ctx.selectFrom(TEST)
            .where(TEST.ACCESS_RIGHT.eq(cast(AccessRight.administrator, TEST.ACCESS_RIGHT)))
            .fetch();
        // Will fail with
        //   ERROR: type "access_right" does not exist(..)
        // because it generates
        //   cast(?::"mcve"."access_right" as access_right)
        // the second access_right should be qualified
    }

    @Test
    public void domainNoCast() {
        ctx.selectFrom(TEST)
            .where(TEST.EXTERNAL_ID.eq("foo"))
            .fetch();
    }

    @Test
    public void domainWithCast() {
        ctx.selectFrom(TEST)
            .where(TEST.EXTERNAL_ID.eq(cast("foo", TEST.EXTERNAL_ID)))
            .fetch();
    }
}
