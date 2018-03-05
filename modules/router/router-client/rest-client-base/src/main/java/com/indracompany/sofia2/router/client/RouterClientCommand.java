/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indracompany.sofia2.router.client;
import com.netflix.hystrix.HystrixCommand;

class RouterClientCommand<T> extends HystrixCommand<T> {

    private final String name;
    private RouterClient<T> routerClient;
   
    private T input;
    private T fallback;
   
    public void setNotification(T input) {
        this.input = input;
    }
    
    public void setFallback(T fallback) {
        this.fallback = fallback;
    }
    
    RouterClientCommand(final String name, final Setter config, RouterClient<T> routerClient) {
        super(config);
        this.name = name;
        this.routerClient = routerClient;
    }

    @Override
    protected T run() {
        return routerClient.execute(input);
    }
    
    @Override
    protected T getFallback() {
        return this.fallback;
    }
}