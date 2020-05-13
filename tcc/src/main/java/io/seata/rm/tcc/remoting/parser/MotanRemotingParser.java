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
package io.seata.rm.tcc.remoting.parser;

import io.seata.common.exception.FrameworkException;
import io.seata.common.util.ReflectionUtil;
import io.seata.rm.tcc.remoting.Protocols;
import io.seata.rm.tcc.remoting.RemotingDesc;

/**
 * @author ph3636
 */
public class MotanRemotingParser extends AbstractedRemotingParser {

    @Override
    public boolean isReference(Object bean, String beanName) throws FrameworkException {
        return "com.weibo.api.motan.config.RefererConfig".equals(bean.getClass().getName());
    }

    @Override
    public boolean isService(Object bean, String beanName) throws FrameworkException {
        return "com.weibo.api.motan.config.ServiceConfig".equals(bean.getClass().getName());
    }

    @Override
    public RemotingDesc getServiceDesc(Object bean, String beanName) throws FrameworkException {
        if (!this.isRemoting(bean, beanName)) {
            return null;
        }
        try {
            RemotingDesc serviceBeanDesc = new RemotingDesc();
            serviceBeanDesc.setInterfaceClass((Class<?>) ReflectionUtil.invokeMethod(bean, "getInterface"));
            serviceBeanDesc.setUniqueId((String) ReflectionUtil.invokeMethod(bean, "getVersion"));
            serviceBeanDesc.setGroup((String) ReflectionUtil.invokeMethod(bean, "getGroup"));
            serviceBeanDesc.setProtocol(getProtocol());
            if (isService(bean, beanName)) {
                serviceBeanDesc.setTargetBean(ReflectionUtil.getFieldValue(bean, "ref"));
            }
            return serviceBeanDesc;
        } catch (Throwable t) {
            throw new FrameworkException(t);
        }
    }

    @Override
    public short getProtocol() {
        return Protocols.MOTAN;
    }
}
