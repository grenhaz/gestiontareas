package org.obarcia.gestiontareas.components.control;

import javafx.scene.control.TextField;

/**
 * Implementación propia para el TextField.
 * 
 * @author obarcia
 */
public class CTextField extends TextField
{
    /**
     * Máximo de caracteres del campo.
     */
    private int maxLength = -1;

    @Override
    public void replaceText(int start, int end, String text)
    {
        if (validate(text))
        {
            // Delete or backspace user input.
            if (text.equals("") || getText() == null || maxLength < 0) {
                super.replaceText(start, end, text);
            } else if (getText().length() < maxLength) {
                super.replaceText(start, end, text);
            }
        }
    }
    @Override
    public void replaceSelection(String text)
    {
        if (validate(text))
        {
            if (text.equals("") || maxLength < 0) {
                super.replaceSelection(text);
            } else if (getText().length() < maxLength) {
                // Add characters, but don't exceed maxlength.
                if (text.length() > maxLength - getText().length()) {
                    text = text.substring(0, maxLength - getText().length());
                }
                super.replaceSelection(text);
            }
        }
    }
    /**
     * Validar el valor del campo.
     * @param text Texto del campo.
     * @return true si es válido, false en caso contrario.
     */
    protected boolean validate(String text)
    {
        return true;
    }
    // ************************************************
    // GETTER & SETTER
    // ************************************************
    public int getMaxLength()
    {
        return maxLength;
    }
    public void setMaxLength(int maxLength)
    {
        this.maxLength = maxLength;
    }
}