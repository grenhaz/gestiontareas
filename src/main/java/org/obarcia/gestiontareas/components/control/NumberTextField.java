package org.obarcia.gestiontareas.components.control;

/**
 * TextField solamente para números.
 * 
 * @author obarcia
 */
public class NumberTextField extends CTextField
{
    @Override
    protected boolean validate(String text)
    {
        if (!text.matches("[0-9]*")) {
            return false;
        }
        
        return super.validate(text);
    }
}