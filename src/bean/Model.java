/**
 * 
 */
package bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Windsor
 *
 */

@Entity
@Table(name="models")
public class Model extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="model_name")
	private String modelName;
	
	@Column(name="uid")
	private Integer userID;

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}
}
