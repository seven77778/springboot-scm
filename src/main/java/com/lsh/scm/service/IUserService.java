package com.lsh.scm.service;

import com.github.pagehelper.PageInfo;
import com.lsh.scm.entity.Scmuser;

public interface IUserService {
    void save(Scmuser user, String[] modelcodes);

    void delete(String account);

    void update(Scmuser user, String[] modelcodes);

    PageInfo<Scmuser> showPage(Integer page);
}
