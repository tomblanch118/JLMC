package ui;

import language.Messages;

/**
 * Classes that want to be informed of changes to the application
 * locale should implement this interface and register themselves
 * with the relevant instance of the {@link Messages} class that
 * is dealing with the locale.
 *  
 * @author tomblanchard
 *
 */
public interface LocalisationListener {
  public void relocalise();
}
