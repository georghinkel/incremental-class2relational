/*
 * SPDX-FileCopyrightText: 2021-2023 The Refinery Authors <https://refinery.tools/>
 *
 * SPDX-License-Identifier: EPL-2.0
 */

/*
 * generated by Xtext 2.25.0
 */
package tools.refinery.language;

import com.google.inject.Binder;
import com.google.inject.name.Names;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.linking.ILinkingService;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.scoping.IGlobalScopeProvider;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.eclipse.xtext.serializer.sequencer.ISemanticSequencer;
import org.eclipse.xtext.validation.IDiagnosticConverter;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.xbase.annotations.validation.DerivedStateAwareResourceValidator;
import tools.refinery.language.conversion.ProblemValueConverterService;
import tools.refinery.language.linking.ProblemLinkingService;
import tools.refinery.language.naming.ProblemQualifiedNameConverter;
import tools.refinery.language.parser.antlr.TokenSourceInjectingProblemParser;
import tools.refinery.language.resource.ProblemDerivedStateComputer;
import tools.refinery.language.resource.ProblemLocationInFileProvider;
import tools.refinery.language.resource.ProblemResource;
import tools.refinery.language.resource.ProblemResourceDescriptionStrategy;
import tools.refinery.language.scoping.ProblemGlobalScopeProvider;
import tools.refinery.language.scoping.ProblemLocalScopeProvider;
import tools.refinery.language.serializer.PreferShortAssertionsProblemSemanticSequencer;
import tools.refinery.language.validation.ProblemDiagnosticConverter;

/**
 * Use this class to register components to be used at runtime / without the
 * Equinox extension registry.
 */
public class ProblemRuntimeModule extends AbstractProblemRuntimeModule {
	@Override
	public Class<? extends IParser> bindIParser() {
		return TokenSourceInjectingProblemParser.class;
	}

	public Class<? extends IQualifiedNameConverter> bindIQualifiedNameConverter() {
		return ProblemQualifiedNameConverter.class;
	}

	public Class<? extends IDefaultResourceDescriptionStrategy> bindIDefaultResourceDescriptionStrategy() {
		return ProblemResourceDescriptionStrategy.class;
	}

	@Override
	public Class<? extends IValueConverterService> bindIValueConverterService() {
		return ProblemValueConverterService.class;
	}

	@Override
	public Class<? extends ILinkingService> bindILinkingService() {
		return ProblemLinkingService.class;
	}

	@Override
	public Class<? extends IGlobalScopeProvider> bindIGlobalScopeProvider() {
		return ProblemGlobalScopeProvider.class;
	}

	@Override
	public void configureIScopeProviderDelegate(Binder binder) {
		binder.bind(IScopeProvider.class).annotatedWith(Names.named(AbstractDeclarativeScopeProvider.NAMED_DELEGATE))
				.to(ProblemLocalScopeProvider.class);
	}

	@Override
	public Class<? extends XtextResource> bindXtextResource() {
		return ProblemResource.class;
	}

	// Method name follows Xtext convention.
	@SuppressWarnings("squid:S100")
	public Class<? extends IResourceDescription.Manager> bindIResourceDescription$Manager() {
		return DerivedStateAwareResourceDescriptionManager.class;
	}

	public Class<? extends IResourceValidator> bindIResourceValidator() {
		return DerivedStateAwareResourceValidator.class;
	}

	public Class<? extends IDerivedStateComputer> bindIDerivedStateComputer() {
		return ProblemDerivedStateComputer.class;
	}

	@Override
	public Class<? extends ILocationInFileProvider> bindILocationInFileProvider() {
		return ProblemLocationInFileProvider.class;
	}

	@Override
	public Class<? extends ISemanticSequencer> bindISemanticSequencer() {
		return PreferShortAssertionsProblemSemanticSequencer.class;
	}

	public Class<? extends IDiagnosticConverter> bindIDiagnosticConverter() {
		return ProblemDiagnosticConverter.class;
	}
}
