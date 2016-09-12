package org.bmleite.benchmark.activejdbc;

import org.javalite.activejdbc.Model;

public interface ModelMapper<T> {

    T map(Model model);

}
