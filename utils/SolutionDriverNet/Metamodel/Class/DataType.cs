//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     Runtime Version:6.0.15
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

using NMF.Collections.Generic;
using NMF.Collections.ObjectModel;
using NMF.Expressions;
using NMF.Expressions.Linq;
using NMF.Models;
using NMF.Models.Collections;
using NMF.Models.Expressions;
using NMF.Models.Meta;
using NMF.Serialization;
using NMF.Utilities;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;

namespace HSRM.TTC2023.ClassToRelational.Class_
{
    
    
    /// <summary>
    /// The default implementation of the DataType class
    /// </summary>
    [XmlNamespaceAttribute("Class")]
    [XmlNamespacePrefixAttribute("class_")]
    [DebuggerDisplayAttribute("DataType {Name}")]
    public partial class DataType : Classifier, HSRM.TTC2023.ClassToRelational.Class_.IDataType, IModelElement
    {
        
        private static NMF.Models.Meta.IClass _classInstance;
        
        /// <summary>
        /// Gets the Class for this model element
        /// </summary>
        public override NMF.Models.Meta.IClass GetClass()
        {
            throw new NotSupportedException();
        }
    }
}

