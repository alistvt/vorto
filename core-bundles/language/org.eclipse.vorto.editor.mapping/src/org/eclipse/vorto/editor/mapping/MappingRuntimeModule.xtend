/**
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
/*
 * generated by Xtext
 */
package org.eclipse.vorto.editor.mapping

import org.eclipse.vorto.editor.mapping.scoping.MappingScopeProvider
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.scoping.IScopeProvider
import org.eclipse.vorto.editor.mapping.formatting.MappingFormatter

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
class MappingRuntimeModule extends AbstractMappingRuntimeModule {
	
	override Class<? extends IScopeProvider> bindIScopeProvider() {
		return MappingScopeProvider;
	}
	
	override Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
		return QualifiedNameWithVersionProvider;
	}
	
	override bindIFormatter(){
		return MappingFormatter
	}
}
