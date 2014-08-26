/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.core.factorys.spring;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.Environment;
import net.hasor.core.Provider;
import net.hasor.core.factorys.AbstractBindInfoFactory;
import net.hasor.core.factorys.AopMatcherMethodInterceptor;
import net.hasor.core.info.AbstractBindInfoProviderAdapter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.google.inject.Binder;
/**
 * 
 * @version : 2014年7月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class SpringRegisterFactory extends AbstractBindInfoFactory {
    private AbstractApplicationContext spring = null;
    //
    public ApplicationContext getSpring() {
        return this.spring;
    }
    /**创建Spring*/
    protected AbstractApplicationContext createSpring(Environment env) {
        ClassPathXmlApplicationContext spring = new ClassPathXmlApplicationContext();
        spring.refresh();
        return spring;
    }
    //
    public <T> T getInstance(BindInfo<T> bindInfo) {
        String name = bindInfo.getBindName();
        Class<T> type = bindInfo.getBindType();
        if (name == null) {
            name = type.getName();
        }
        return (T) this.spring.getBean(name, type);
    }
    //
    /*------------------------------------------------------------------------------add to Spring*/
    public void doInitialize(ApiBinder apiBinder) {
        super.doInitialize(apiBinder);
        apiBinder.bindType(ApplicationContext.class).toProvider(new Provider<ApplicationContext>() {
            public ApplicationContext get() {
                return getSpring();
            }
        });
    }
    //
    public void doInitializeCompleted(Object context) {
        //1.创建Spring
        AppContext appContext = (AppContext) context;
        this.spring = this.createSpring(appContext.getEnvironment());
        //2.
        super.doInitializeCompleted(appContext);
    }
    protected void configBindInfo(AbstractBindInfoProviderAdapter<?> bindInfo, Object context) {
        AbstractBindInfoProviderAdapter<Object> regObject = (AbstractBindInfoProviderAdapter<Object>) bindInfo;
        if (regObject.getCustomerProvider() != null) {
            //单例Bean
            this.registerProvider(regObject);
        } else {
            //注册Bean
            this.registerBean(regObject);
        }
    }
    //
    //处理Aop配置
    private void configAopRegister(final AbstractBindInfoProviderAdapter<Object> register, final Binder binder) {
        if (register.getBindType().isAssignableFrom(AopMatcherMethodInterceptor.class) == false) {
            return;
        }
    }
    //
    //处理带有CustomerProvider配置
    private void registerProvider(AbstractBindInfoProviderAdapter<?> regObject) {
        String bindID = regObject.getBindID();
        //
        ConfigurableListableBeanFactory factory = this.spring.getBeanFactory();
        BeanDefinitionRegistry defineRegistry = (BeanDefinitionRegistry) factory;
        BeanDefinitionBuilder defineBuilder = BeanDefinitionBuilder.genericBeanDefinition(SpringCustomerBean.class);
        if (regObject.isSingleton() == true) {
            defineBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        }
        BeanDefinition define = defineBuilder.getRawBeanDefinition();
        define.setAttribute("RegObject", regObject);
        defineRegistry.registerBeanDefinition(bindID, define);
    }
    //
    //处理一般配置
    private void registerBean(AbstractBindInfoProviderAdapter<?> regObject) {
        String bindID = regObject.getBindID();
        Class<?> regType = regObject.getSourceType();
        if (regType == null) {
            regType = regObject.getBindType();
        }
        //
        ConfigurableListableBeanFactory factory = this.spring.getBeanFactory();
        BeanDefinitionRegistry defineRegistry = (BeanDefinitionRegistry) factory;
        BeanDefinitionBuilder define = BeanDefinitionBuilder.genericBeanDefinition(regType);
        if (regObject.isSingleton() == true) {
            define.setScope(BeanDefinition.SCOPE_SINGLETON);
        }
        defineRegistry.registerBeanDefinition(bindID, define.getRawBeanDefinition());
    }
}