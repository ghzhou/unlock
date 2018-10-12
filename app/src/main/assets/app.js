'use strict';
const e = React.createElement;

class Digit extends React.Component {
  constructor(props){
    super(props);
  }
  render() {
    return e(
      'div',
      {
        onClick: () => this.props.onDigitPressed(this.props.digit),
        class: 'digit'
      },
      this.props.digit
    );
  }
}

class Row extends React.Component {
  render() {
    return e('div', {
      style: {
        margin: 'auto'
      }
    }, this.props.items.map(d => e(Digit,{
      digit: d,
      onDigitPressed: this.props.onDigitPressed,
    })));
  }
}

class App extends React.Component {
  constructor(props) {
    super(props)
    this.digitSequence = '';
    this.onDigitPressed = (digit) => {
      if(digit==='*') {
        var xhttp = new XMLHttpRequest();
        xhttp.open("get", "/?username="+this.digitSequence, true);
        xhttp.send();
        this.digitSequence = '';
        return;
      }
      this.digitSequence += digit;
    };
  }

  render() {
    let matrix = [
      ['1','2','3'],
      ['4','5','6'],
      ['7','8','9'],
      ['#','0','*']
    ];
    return e('div',{
       style: {
         display: 'flex',
         flexDirection: 'column'
       }
    }, matrix.map(items=>e(Row, {
      onDigitPressed: this.onDigitPressed,
      items: items
    })));
  }
}

const domContainer = document.querySelector('#app_container');
ReactDOM.render(e(App), domContainer);