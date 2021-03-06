/*
 * Copyright 2017 the original author or authors.
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

package org.gradle.language.swift.internal;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.attributes.Usage;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.language.nativeplatform.internal.Names;
import org.gradle.language.swift.SwiftBinary;

import static org.gradle.language.cpp.CppBinary.DEBUGGABLE_ATTRIBUTE;

public class DefaultSwiftBinary implements SwiftBinary {
    private final String name;
    private final Provider<String> module;
    private final boolean debuggable;
    private final FileCollection source;
    private final FileCollection importPath;
    private final FileCollection linkLibs;
    private final Configuration runtimeLibs;

    public DefaultSwiftBinary(String name, ObjectFactory objectFactory, Provider<String> module, boolean debuggable, FileCollection source, ConfigurationContainer configurations, Configuration implementation) {
        this.name = name;
        this.module = module;
        this.debuggable = debuggable;
        this.source = source;

        Names names = Names.of(name);

        // TODO - reduce duplication with C++ binary
        Configuration importPathConfig = configurations.maybeCreate(names.withPrefix("swiftImport"));
        importPathConfig.extendsFrom(implementation);
        importPathConfig.setCanBeConsumed(false);
        importPathConfig.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, objectFactory.named(Usage.class, Usage.SWIFT_API));
        importPathConfig.getAttributes().attribute(DEBUGGABLE_ATTRIBUTE, debuggable);

        Configuration nativeLink = configurations.maybeCreate(names.withPrefix("nativeLink"));
        nativeLink.extendsFrom(implementation);
        nativeLink.setCanBeConsumed(false);
        nativeLink.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, objectFactory.named(Usage.class, Usage.NATIVE_LINK));
        nativeLink.getAttributes().attribute(DEBUGGABLE_ATTRIBUTE, debuggable);

        Configuration nativeRuntime = configurations.maybeCreate(names.withPrefix("nativeRuntime"));
        nativeRuntime.extendsFrom(implementation);
        nativeRuntime.setCanBeConsumed(false);
        nativeRuntime.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, objectFactory.named(Usage.class, Usage.NATIVE_RUNTIME));
        nativeRuntime.getAttributes().attribute(DEBUGGABLE_ATTRIBUTE, debuggable);

        importPath = importPathConfig;
        linkLibs = nativeLink;
        runtimeLibs = nativeRuntime;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Provider<String> getModule() {
        return module;
    }

    @Override
    public boolean isDebuggable() {
        return debuggable;
    }

    @Override
    public FileCollection getSwiftSource() {
        return source;
    }

    @Override
    public FileCollection getCompileImportPath() {
        return importPath;
    }

    @Override
    public FileCollection getLinkLibraries() {
        return linkLibs;
    }

    @Override
    public FileCollection getRuntimeLibraries() {
        return runtimeLibs;
    }
}
