package com.alibaba.fescar.core.protocol.transaction;

import com.alibaba.fescar.core.model.BranchStatus;
import com.alibaba.fescar.core.protocol.ResultCode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author: jimin.jm@alibaba-inc.com
 * @date 2019/04/16
 */
public class BranchRollbackResponseTest {
    @Test
    public void toStringTest() {
        BranchRollbackResponse branchRollbackResponse = new BranchRollbackResponse();
        branchRollbackResponse.setXid("127.0.0.1:8091:123456");
        branchRollbackResponse.setBranchId(2345678L);
        branchRollbackResponse.setBranchStatus(BranchStatus.PhaseOne_Done);
        branchRollbackResponse.setResultCode(ResultCode.Success);
        branchRollbackResponse.setMsg("");
        Assert.assertEquals(
            "xid=127.0.0.1:8091:123456,branchId=2345678,branchStatus=PhaseOne_Done,result code =Success,getMsg =",
            branchRollbackResponse.toString());

    }

    @Test
    public void testEncodeDecode() {
        BranchRollbackResponse branchRollbackResponse = new BranchRollbackResponse();

        branchRollbackResponse.setXid("127.0.0.1:9999:39875642");
        branchRollbackResponse.setBranchId(10241024L);
        branchRollbackResponse.setResultCode(ResultCode.Success);
        branchRollbackResponse.setBranchStatus(BranchStatus.PhaseTwo_Committed);

        byte[] encodeResult = branchRollbackResponse.encode();

        ByteBuf byteBuffer = UnpooledByteBufAllocator.DEFAULT.directBuffer(encodeResult.length);
        byteBuffer.writeBytes(encodeResult);

        BranchRollbackResponse decodeBranchRollbackResponse = new BranchRollbackResponse();
        decodeBranchRollbackResponse.decode(byteBuffer);
        Assert.assertEquals(decodeBranchRollbackResponse.getXid(), branchRollbackResponse.getXid());
        Assert.assertEquals(decodeBranchRollbackResponse.getBranchId(), branchRollbackResponse.getBranchId());
        Assert.assertEquals(decodeBranchRollbackResponse.getResultCode(), branchRollbackResponse.getResultCode());
        Assert.assertEquals(decodeBranchRollbackResponse.getBranchStatus(), branchRollbackResponse.getBranchStatus());
        Assert.assertEquals(decodeBranchRollbackResponse.getTransactionExceptionCode(),
            branchRollbackResponse.getTransactionExceptionCode());
        Assert.assertEquals(decodeBranchRollbackResponse.getMsg(), branchRollbackResponse.getMsg());
    }

}