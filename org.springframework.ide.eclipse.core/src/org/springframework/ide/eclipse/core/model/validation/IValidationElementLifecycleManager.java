package org.springframework.ide.eclipse.core.model.validation;

import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.springframework.ide.eclipse.core.model.IResourceModelElement;

/**
 * Lifecycle interface that can be implemented to manage init and destroy
 * operations on underlying resource.
 * <p>
 * NOTE: Methods will be called in the following order.
 * <ul>
 * <li>{@link #init(IResource)}</li>
 * <li>{@link #getRootElement()}</li>
 * <li>{@link #getContextElements()}</li>
 * <li>{@link #destory()}</li>
 * </ul>
 * @author Christian Dupuis
 * @since 2.0.2
 */
public interface IValidationElementLifecycleManager {

	/**
	 * Inits this {@link IValidationElementLifecycleManager}.
	 * <p>
	 * Implementors must implement this method for opening expensive resources.
	 * @param resource the {@link IResource} that is currently being validated
	 */
	void init(IResource resource);

	/**
	 * Returns a list of {@link IResourceModelElement context element}s for the
	 * root element which should be used during validation.
	 */
	Set<IResourceModelElement> getContextElements();

	/**
	 * Returns the {@link IResourceModelElement root element} for the given
	 * {@link IResource} which should be visited by the validator.
	 */
	IResourceModelElement getRootElement();

	/**
	 * Closes the lifecycle of the managed resource(s).
	 */
	void destory();
}