<%--
  GRANITE DATA SERVICES
  Copyright (C) 2007-2008 ADEQUATE SYSTEMS SARL

  This file is part of Granite Data Services.

  Granite Data Services is free software; you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 3 of the License, or (at your
  option) any later version.

  Granite Data Services is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
  for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, see <http://www.gnu.org/licenses/>.

  @author Franck WOLFF
--%><%

    Set as3Imports = new TreeSet();

    if (!jClass.hasSuperclass())
        as3Imports.add("flash.utils.IExternalizable");

    for (jImport in jClass.imports) {
        if (jImport.as3Type.hasPackage()) {
            // BlazeDS uses ArrayCollections instead of ListCollectionVies
            if(jImport.as3Type.qualifiedName == "mx.collections.ListCollectionView") {
            }
            // BlazeDS uses Object instead of IMap for communicating Maps.
            // As Object needs no import, simply omit the import.
            else if(jImport.as3Type.qualifiedName == "org.granite.collections.IMap") {
            }
            // We don't need imports of the same package.
            else {
                if(jImport.as3Type.packageName != jClass.as3Type.packageName) {
                    as3Imports.add(jImport.as3Type.qualifiedName);
                }
            }
        }
    }

%>/**
 * Generated by Gas3 v${gVersion} (Granite Data Services).
 *
 * WARNING: DO NOT CHANGE THIS FILE. IT MAY BE OVERRIDDEN EACH TIME YOU USE
 * THE GENERATOR. CHANGE INSTEAD THE INHERITED CLASS (${jClass.as3Type.name}.as).
 */

package ${jClass.as3Type.packageName} {
<%
///////////////////////////////////////////////////////////////////////////////
// Write Import Statements.

    for (as3Import in as3Imports) {%>
    import ${as3Import};<%
    }

///////////////////////////////////////////////////////////////////////////////
// Write Class Declaration.%>

    public class ${jClass.as3Type.name}Base<%

        if (jClass.hasSuperclass()) {
            %> extends ${jClass.superclass.as3Type.name}<%
        }

        boolean implementsWritten = false;
        for (jInterface in jClass.interfaces) {
            if (!implementsWritten) {
                %> implements ${jInterface.as3Type.name}<%

                implementsWritten = true;
            } else {
                %>, ${jInterface.as3Type.name}<%
            }
        }

    %> {

    public function ${jClass.as3Type.name}Base() {}

<%


    ///////////////////////////////////////////////////////////////////////////
    // Write Private Fields.

    for (jProperty in jClass.properties) {
        if(jProperty.as3Type.name == "ListCollectionView") { %>
        private var _${jProperty.name}:Array;<%
        } else if(jProperty.as3Type.name == "IMap") { %>
        private var _${jProperty.name}:Object;<%
        } else { %>
        private var _${jProperty.name}:${jProperty.as3Type.name};<%
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Write Public Getter/Setter.

    for (jProperty in jClass.properties) {
        if (jProperty.readable || jProperty.writable) {%>
<%
            if (jProperty.writable) {
                if(jProperty.as3Type.name == "ListCollectionView") {%>
        public function set ${jProperty.name}(value:Array):void {
            _${jProperty.name} = value;
        }<%
                } else if(jProperty.as3Type.name == "IMap") {%>
        public function set ${jProperty.name}(value:Object):void {
            _${jProperty.name} = value;
        }<%
                } else {%>
        public function set ${jProperty.name}(value:${jProperty.as3Type.name}):void {
            _${jProperty.name} = value;
        }<%
                }
            }
            if (jProperty.readable) {
                if(jProperty.as3Type.name == "ListCollectionView") {%>
        public function get ${jProperty.name}():Array {
            return _${jProperty.name};
        }<%
                } else if(jProperty.as3Type.name == "IMap") {%>
        public function get ${jProperty.name}():Object {
            return _${jProperty.name};
        }<%
                } else {%>
        public function get ${jProperty.name}():${jProperty.as3Type.name} {
            return _${jProperty.name};
        }<%
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Write Public Getters/Setters for Implemented Interfaces.

    if (jClass.hasInterfaces()) {
        for (jProperty in jClass.interfacesProperties) {
            if (jProperty.readable || jProperty.writable) {%>
<%
                if (jProperty.writable) {%>
        public function set ${jProperty.name}(value:${jProperty.as3Type.name}):void {
        }<%
                }
                if (jProperty.readable) {%>
        public function get ${jProperty.name}():${jProperty.as3Type.name} {
            return ${jProperty.as3Type.nullValue};
        }<%
                }
            }
        }
    }%>
    }
}