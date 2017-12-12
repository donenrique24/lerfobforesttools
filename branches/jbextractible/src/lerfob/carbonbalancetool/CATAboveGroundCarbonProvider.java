/*
 * This file is part of the lerfob-forestools library.
 *
 * Copyright (C) 2010-2013 Mathieu Fortin AgroParisTech/INRA UMR LERFoB, 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed with the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * Please see the license at http://www.gnu.org/copyleft/lesser.html.
 */
package lerfob.carbonbalancetool;

/**
 * This interface ensures that the instance can provide its above ground carbon in Mg.
 * @author Mathieu Fortin - August 2013
 */
public interface CATAboveGroundCarbonProvider {

	/**
	 * This method returns the aboveground carbon of the instance in Mg WITHOUT expansion factor.
	 * @return a double
	 */
	public double getAboveGroundCarbonMg();
	
}
