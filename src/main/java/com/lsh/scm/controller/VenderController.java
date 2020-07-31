package com.lsh.scm.controller;

import java.util.List;

import org.apache.ibatis.transaction.TransactionException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lsh.scm.constants.BusinessStatus;
import com.lsh.scm.dao.VenderMapper;
import com.lsh.scm.entity.Vender;
import com.lsh.scm.entity.response.ResponseMessage;
import com.lsh.scm.util.DateUtil;
import com.lsh.scm.util.StringUtil;

import javax.annotation.Resource;

@RestController
@RequestMapping("/main/purchase")
public class VenderController {
    @Resource
    private VenderMapper venderMapper;

    private ResponseMessage rm;

    public boolean checkVender(Vender vender) {
        if (StringUtil.isEmpty(vender)) {
            rm.setMessage("用户信息不能为空");
            return false;
        }
        String venderCode = vender.getVenderCode();
        if (StringUtil.isEmpty(venderCode)) {
            rm.setMessage("供应商编号venderCode参数：不能为空");
            return false;
        } else if (!venderCode.matches("^[a-zA-Z0-9]{4,20}$")) {
            rm.setMessage("供应商编号venderCode参数：要求4-20位的数字字母");
            return false;
        }
        String name = vender.getName();
        if (StringUtil.isEmpty(name)) {
            rm.setMessage("名称name参数:不能为空");
            return false;
        } else if (name.length() > 100) {
            rm.setMessage("名称name参数：长度不能超过100个字符");
            return false;
        }
        String password = vender.getPassWord();
        if (StringUtil.isEmpty(password)) {
            rm.setMessage("密码password参数：不能为空");
            return false;
        } else if (password.length() > 20 || password.length() < BusinessStatus.FAIL) {
            rm.setMessage("密码password参数：长度为4~20位");
            return false;
        }

        String address = vender.getAddress();
        if (StringUtil.isEmpty(address)) {
            rm.setMessage("地址address参数：不能为空");
            return false;
        }
        String createDate = vender.getCreateDate();

        if (StringUtil.isEmpty(createDate)) {
            rm.setMessage("注册日期createDate参数：不能为空");
            return false;
        } else if (!DateUtil.isValid(createDate)) {
            rm.setMessage("注册日期createDate参数：日期格式不正确");
            return false;
        }

        String tel = vender.getTel();
        if (StringUtil.isEmpty(tel)) {
            rm.setMessage("电话tel参数：不能为空");
            return false;
        }

        return true;
    }

    @RequestMapping("/vender/add")
    public ResponseMessage venderAdd(Vender vender) {
        rm = new ResponseMessage();
        boolean flag = checkVender(vender);
        if (!flag) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            return rm;
        }
        venderMapper.addVender(vender);
        rm.setCode(BusinessStatus.SUCCESS);
        rm.setMessage("添加供应商成功");
        return rm;
    }


    @RequestMapping("/vender/delete")
    public ResponseMessage delete(String venderCode) {
        rm = new ResponseMessage();
        if (venderCode == null) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            rm.setMessage("缺少必要的参数：venderCode");
        } else {
            int n = venderMapper.deleteVender(venderCode);
            if (n == 0) throw new TransactionException("供应商编号不存在或存在依赖的采购单，删除失败");
            rm.setCode(BusinessStatus.SUCCESS);
            rm.setMessage("删除成功");
            rm.setData(venderMapper.select(null));
        }
        return rm;
    }

    @RequestMapping("/vender/update")
    public ResponseMessage update(Vender vender) {
        rm = new ResponseMessage();
        boolean flag = checkVender(vender);
        if (!flag) {
            rm.setCode(BusinessStatus.PRAMA_ERROR);
            return rm;
        }
        int n = venderMapper.updateVender(vender);
        if (n == 0) {
            rm.setCode(BusinessStatus.FAIL);
            rm.setMessage("修改失败");
        } else {
            rm.setCode(BusinessStatus.SUCCESS);
            rm.setMessage("修改成功");
        }
        return rm;
    }

    @RequestMapping("/vender/show")
    public PageInfo<Vender> select(Vender vender, Integer page) {
        PageHelper.startPage(page == null ? 1 : page, 10);
        List<Vender> venders = venderMapper.select(vender);
        PageInfo<Vender> info = new PageInfo<>(venders);
        return info;
    }

    @RequestMapping("/vender/all")
    public List<Vender> selectAll() {
        List<Vender> venders = venderMapper.select(null);
        return venders;
    }
}
