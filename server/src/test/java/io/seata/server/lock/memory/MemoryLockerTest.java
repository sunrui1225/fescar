/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.seata.server.lock.memory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import io.seata.core.exception.TransactionException;
import io.seata.server.lock.DefaultLockManager;
import io.seata.server.session.BranchSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author jimin.jm@alibaba-inc.com
 * @date 2019/10/11
 */
class MemoryLockerTest {

    private BranchSession branchSession1;
    private BranchSession branchSession2;
    private DefaultLockManager lockManager;

    @BeforeEach
    void setUp() {
        branchSession1 = new BranchSession();
        branchSession1.setXid("192.168.0.1:8091:123456");
        branchSession1.setTransactionId(123456L);
        branchSession1.setBranchId(123457L);
        branchSession1.setResourceId("jdbc:mysql://mockUrl:3306/mockDb1");
        branchSession1.setLockKey("tb1:1,2,3,4,5;tb2:1,2,5,6,7");

        branchSession2 = new BranchSession();
        branchSession2.setXid("192.168.0.1:8091:123458");
        branchSession2.setTransactionId(123458L);
        branchSession2.setBranchId(123459L);
        branchSession2.setResourceId("jdbc:mysql://mockUrl:3306/mockDb2");
        branchSession2.setLockKey("tb1:1,2,3,4,5;tb2:1,2,5,6,7");
    }

    @AfterEach
    void tearDown() throws TransactionException {
        lockManager.cleanAllLocks();
    }

    @Test
    void testLockSize() throws TransactionException {
        lockManager = new DefaultLockManager();
        lockManager.acquireLock(branchSession1);
        Assertions.assertEquals(10, getLockSize(branchSession1));
        lockManager.acquireLock(branchSession2);
        Assertions.assertEquals(20, getLockSize(branchSession1) + getLockSize(branchSession2));
        branchSession1.unlock();
        Assertions.assertEquals(10, getLockSize(branchSession2));
        branchSession2.unlock();
    }

    private int getLockSize(BranchSession branchSession) {
        int size = 0;
        ConcurrentMap<ConcurrentMap<String, Long>, Set<String>> bucketHolder = branchSession.getLockHolder();
        for (Map.Entry<ConcurrentMap<String, Long>, Set<String>> entry : bucketHolder.entrySet()) {
            //- $$table.id$$ size
            size += entry.getKey().size() - 1;
        }
        return size;
    }
}