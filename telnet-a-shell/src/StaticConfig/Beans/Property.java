/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package StaticConfig.Beans;

/**
 *
 * @author zaltair
 */
public class Property <E> {

   E value;
   String name;
   String description;

    public Property(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Property(E value, String name, String description) {
        this.value = value;
        this.name = name;
        this.description = description;
    }

   

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public E getValue() {
        return value;
    }

    public Property<E> setValue(E value) {
        this.value = value;
        return this;
    }



}
